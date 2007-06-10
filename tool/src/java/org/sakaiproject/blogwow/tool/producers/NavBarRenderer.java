package org.sakaiproject.blogwow.tool.producers;

import org.sakaiproject.blogwow.logic.BlogLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.tool.params.BlogEntryParams;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class NavBarRenderer {

    private ExternalLogic externalLogic;
    private BlogLogic blogLogic;

    public void makeNavBar(UIContainer tofill, String divID, String currentViewID) {
        BlogWowBlog blog = blogLogic.getBlogByLocationAndUser(externalLogic.getCurrentLocationId(), externalLogic.getCurrentUserId());

        UIJointContainer joint = new UIJointContainer(tofill, divID, "blog-wow-navigation:", ""+1);

        if (blog != null) {
            UILink.make(joint, "item:icon", "../images/page_white_edit.png");
            if (currentViewID.equals(AddEntryProducer.VIEW_ID)) {
                UIMessage.make(joint, "item:text", "blogwow.navbar.add");
            } else {
                // user cannot create blog so no add entry link
                UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.add"), 
                        new BlogEntryParams(AddEntryProducer.VIEW_ID, blog.getId(), null));               
            }
            UIOutput.make(joint, "item:separator");
        }

        UILink.make(joint, "item:icon", "../images/page_white_edit.png");
        if (currentViewID.equals(HomeProducer.VIEW_ID)) {
            UIMessage.make(joint, "item:text", "blogwow.navbar.bloglist");
        } else {
            UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.bloglist"), new SimpleViewParameters(HomeProducer.VIEW_ID));
        }
        UIOutput.make(joint, "item:separator");

        if (blog != null) {
            UILink.make(joint, "item:icon", "../images/cog.png");
            if (currentViewID.equals(MySettingsProducer.VIEW_ID)) {
                UIMessage.make(joint, "item:text", "blogwow.navbar.settings");
            } else {
                UIInternalLink.make(joint, "item:link", 
                        UIMessage.make("blogwow.navbar.settings"), 
                            new SimpleBlogParams(MySettingsProducer.VIEW_ID, blog.getId()));
            }
            UIOutput.make(joint, "item:separator");
        }

        UILink.make(joint, "item:icon", "../images/group_gear.png");
        if (currentViewID.equals(PermissionsProducer.VIEW_ID))
            UIMessage.make(joint, "item:text", "blogwow.navbar.permissions");
        else
            UIInternalLink.make(joint, "item:link", UIMessage.make("blogwow.navbar.permissions"), new SimpleViewParameters(PermissionsProducer.VIEW_ID));  

    }

    public void setBlogLogic(BlogLogic blogLogic) {
        this.blogLogic = blogLogic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

}
