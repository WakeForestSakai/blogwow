/******************************************************************************
 * EntryLogicImpl.java - created by aaronz on Jun 2, 2007
 * 
 * Copyright (c) 2007 Centre for Academic Research in Educational Technologies
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.blogwow.dao.BlogWowDao;
import org.sakaiproject.blogwow.logic.EntryLogic;
import org.sakaiproject.blogwow.logic.ExternalLogic;
import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowComment;
import org.sakaiproject.blogwow.model.BlogWowEntry;
import org.sakaiproject.genericdao.api.finders.ByPropsFinder;

/**
 * Implementation
 * @author Sakai App Builder -AZ
 */
public class EntryLogicImpl implements EntryLogic {

	private static Log log = LogFactory.getLog(EntryLogicImpl.class);

	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private BlogWowDao dao;
	public void setDao(BlogWowDao dao) {
		this.dao = dao;
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#canWriteEntry(java.lang.Long, java.lang.String)
	 */
	public boolean canWriteEntry(Long entryId, String userId) {
		if ( externalLogic.isUserAdmin(userId) ) {
			// the system super user can write
			return true;
		}

		BlogWowEntry entry = getEntryById(entryId);
		BlogWowBlog blog = entry.getBlog();
		if (blog.getOwnerId().equals( userId ) &&
				externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation()) ) {
			// blog owner can write
			return true;
		} else if (entry.getOwnerId().equals( userId ) &&
				externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE, blog.getLocation()) ) {
			// entry owner can write
			return true;
		} else if ( externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_WRITE_ANY, blog.getLocation()) ) {
			// users with permission in the specified location can write for that location
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#getAllVisibleEntries(java.lang.Long, java.lang.String, java.lang.String, boolean, int, int)
	 */
	public List getAllVisibleEntries(Long blogId, String userId, String sortProperty, boolean ascending, int start,
			int limit) {
		return getAllVisibleEntries(new Long[] {blogId}, userId, sortProperty, ascending, start, limit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#getAllVisibleEntries(java.lang.Long[], java.lang.String, java.lang.String, boolean, int, int)
	 */
	public List getAllVisibleEntries(Long[] blogIds, String userId, String sortProperty, boolean ascending, int start,
			int limit) {

		if (sortProperty == null) {
			sortProperty = "dateCreated";
			ascending = false;
		}

		if (! ascending) {
			sortProperty += ByPropsFinder.DESC;
		}

		List l = new ArrayList();
		if ( externalLogic.isUserAdmin(userId) ) {
			l = dao.findByProperties(BlogWowEntry.class, 
					new String[] {"blog.id"}, 
					new Object[] {blogIds},
					new int[] {ByPropsFinder.EQUALS},
					new String[] {sortProperty},
					start, limit);
		} else {
			List locations = dao.getLocationsForBlogsIds(blogIds);
			// check current user perms on these locations, remove the ones they do not have full perms in
			for (Iterator iter = locations.iterator(); iter.hasNext();) {
				String location = (String) iter.next();
				if (! externalLogic.isUserAllowedInLocation(userId, ExternalLogic.BLOG_ENTRY_READ_ANY, location)) {
					iter.remove();
				}
			}
			String[] locsArray = (String[]) locations.toArray(new String[] {});

			/*
			 * rules to determine which entries to get
			 * 1) entry.id is in blogIds AND
			 * 2) entry.blog.owner is userId OR
			 * 3) entry.owner is userId OR
			 * 4) entry.privacy is public OR
			 * 5) (entry.privacy is group AND location is in new location array)
			 */
			l = dao.getBlogEntries(blogIds, userId, locsArray, sortProperty, ascending, start, limit);
		}

		return l;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#getEntryById(java.lang.Long)
	 */
	public BlogWowEntry getEntryById(Long entryId) {
		return (BlogWowEntry) dao.findById(BlogWowEntry.class, entryId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#removeEntry(java.lang.Long)
	 */
	public void removeEntry(Long entryId) {
		BlogWowEntry entry = getEntryById(entryId);
		List l = dao.findByProperties(BlogWowComment.class, 
				new String[] {"entry.id"}, 
				new Object[] {entryId});
		if (l.size() == 0) {
			dao.delete(entry);
		} else {
			Set[] entitySets = new HashSet[2];
			entitySets[0] = new HashSet();
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				BlogWowComment comment = (BlogWowComment) iter.next();
				entitySets[0].add(comment);
			}

			entitySets[1] = new HashSet();
			entitySets[1].add(entry);

			dao.deleteMixedSet(entitySets);			
		}
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.blogwow.logic.EntryLogic#saveEntry(org.sakaiproject.blogwow.model.BlogWowEntry)
	 */
	public void saveEntry(BlogWowEntry entry) {
		entry.setDateModified(new Date());
		// set the owner to current if not set
		if (entry.getOwnerId() == null) {
			entry.setOwnerId( externalLogic.getCurrentUserId() );
		}
		if (entry.getDateCreated() == null) {
			entry.setDateCreated( new Date() );
		}
		// save entry if new OR check if the current user can update the existing item
		if ( (entry.getId() == null) || 
				canWriteEntry(entry.getId(), externalLogic.getCurrentUserId()) ) {
			dao.save(entry);
			log.info("Saving entry: " + entry.getId() + ":" + entry.getText());
		} else {
			throw new SecurityException("Current user cannot save entry " + 
					entry.getId() + " because they do not have permission");
		}
	}

}