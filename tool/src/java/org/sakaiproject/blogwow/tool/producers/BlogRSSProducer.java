package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.blogwow.tool.params.BlogRssViewParams;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class BlogRSSProducer implements 
ViewComponentProducer, 
ViewParamsReporter,
ContentTypeReporter
{
    public static final String VIEW_ID = "blog_rss";
    public String getViewID() {
        return VIEW_ID;
    }

    private BlogLogic blogLogic;
    private EntryLogic entryLogic;
    private ExternalLogic externalLogic;


    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
        String currentUserId = externalLogic.getCurrentUserId();

        BlogRssViewParams params = (BlogRssViewParams) viewparams;
        String blogId = params.blogId;
        String locationId = params.locationId;

        List<BlogWowEntry> entries = new ArrayList<BlogWowEntry>();
        // get the entries for a single blog or a group of blogs depending on what is passed in
        if (blogId != null) {
            BlogWowBlog blog = blogLogic.getBlogById( blogId );
            UIOutput.make(tofill, "channel-title", blog.getTitle());
            entries = entryLogic.getAllVisibleEntries(blogId, currentUserId, null, true, 0, 10);                        
        } else if (locationId != null) {
            UIOutput.make(tofill, "channel-title", externalLogic.getLocationTitle(locationId));
            List<BlogWowBlog> blogs = blogLogic.getAllVisibleBlogs(locationId, null, true, 0, 10);
            String[] blogIds = new String[blogs.size()];
            for (int i=0; i<blogs.size(); i++) {
                blogIds[i] = (blogs.get(i)).getId();
            }
            entries = entryLogic.getAllVisibleEntries(blogIds, currentUserId, null, true, 0, 10);                        
        }

        for (int i = 0; i < entries.size(); i++) {
            BlogWowEntry entry = entries.get(i);
            UIBranchContainer rssitem = UIBranchContainer.make(tofill, "item:", i+"");
            UIOutput.make(rssitem, "item-title", entry.getTitle());
            UIOutput.make(rssitem, "creator", entry.getOwnerId());

            String desc = "<![CDATA[" 
                + ( entry.getText().length() < 200 
                        ? entry.getText().substring(0, entry.getText().length())
                                : entry.getText().substring(0, 200)) 
                                + "]]>";
            String content = "<![CDATA[" + entry.getText() + "]]>";
            UIVerbatim.make(rssitem, "description", desc);
            UIVerbatim.make(rssitem, "content" ,content);
        }
    }

    public ViewParameters getViewParameters() {
        return new BlogRssViewParams();
    }


    public String getContentType() {
        return ContentTypeInfoRegistry.RSS_2;
    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setEntryLogic(EntryLogic entryLogic) {
        this.entryLogic = entryLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
