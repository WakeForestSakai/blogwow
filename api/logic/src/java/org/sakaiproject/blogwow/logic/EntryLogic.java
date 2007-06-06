/******************************************************************************
 * EntryLogic.java - created by Sakai App Builder -AZ
 * 
 * Copyright (c) 2007 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

package org.sakaiproject.blogwow.logic;

import java.util.List;

import org.sakaiproject.blogwow.model.BlogWowBlog;
import org.sakaiproject.blogwow.model.BlogWowEntry;

/**
 * Logic for handling blog entries
 * @author Sakai App Builder -AZ
 */
public interface EntryLogic {

	/**
	 * Get a blog entry by its unique id
	 * @param entryId a unique id for a {@link BlogWowEntry}
	 * @return a blog wow entry or null if not found
	 */
	public BlogWowEntry getEntryById(Long entryId);

	/**
	 * Create or update a blog entry
	 * @param entry a blog entry object
	 */
	public void saveEntry(BlogWowEntry entry);

	/**
	 * Remove a blog entry and all associated comments
	 * @param entryId a unique id for a {@link BlogWowEntry}
	 */
	public void removeEntry(Long entryId);

	/**
	 * Get all blog entries which are visible to a specific user
	 * @param blogId unique id of a {@link BlogWowBlog}
	 * @param userId the internal user id (not username), if null then return all entries regardless of visibility
	 * @param sortProperty the name of the {@link BlogWowEntry} property to sort on
	 * or null to sort by default property (dateCreated desc)
	 * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
	 * @param start the entry number to start on (based on current sort rules), first entry is 0
	 * @param limit the maximum number of entries to return, 0 returns as many entries as possible
	 * @return a list of {@link BlogWowEntry} objects
	 */
	public List<BlogWowEntry> getAllVisibleEntries(Long blogId, String userId, String sortProperty, boolean ascending, int start, int limit);

	/**
	 * Efficiency method which gets all blog entries which are visible to a specific user for an array of blogs
	 * @param blogIds an array of unique ids of {@link BlogWowBlog}
	 * @param userId the internal user id (not username), if null then return all entries regardless of visibility
	 * @param sortProperty the name of the {@link BlogWowEntry} property to sort on
	 * or null to sort by default property (dateCreated desc)
	 * @param ascending sort in ascending order, if false then descending (ignored if sortProperty is null)
	 * @param start the entry number to start on (based on current sort rules), first entry is 0
	 * @param limit the maximum number of entries to return, 0 returns as many entries as possible
	 * @return a list of {@link BlogWowEntry} objects
	 */
	public List<BlogWowEntry> getAllVisibleEntries(Long[] blogIds, String userId, String sortProperty, boolean ascending, int start, int limit);

	/**
	 * Check if an entry can be updated or removed
	 * @param entryId a unique id for a {@link BlogWowEntry}
	 * @param userId the internal user id (not username)
	 * @return true if this entry can be udpated or removed by this user, false otherwise
	 */
	public boolean canWriteEntry(Long entryId, String userId);

}
