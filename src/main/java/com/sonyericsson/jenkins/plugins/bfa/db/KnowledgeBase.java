/*
 * The MIT License
 *
 * Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonyericsson.jenkins.plugins.bfa.db;

import com.sonyericsson.jenkins.plugins.bfa.model.FailureCause;
import hudson.ExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

import java.io.Serializable;
import java.util.Collection;

/**
 * Base class for storage implementations of {@link FailureCause}s. Extend this class and put <code>@Extension</code> on
 * the descriptor to provide your own.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public abstract class KnowledgeBase implements Describable<KnowledgeBase>, Serializable {

    /**
     * Get the list of {@link FailureCause}s. It is intended to be used in the scanning phase hence it should be
     * returned as quickly as possible, so the list could be cached.
     *
     * @return the full list of causes.
     */
    public abstract Collection<FailureCause> getCauses();

    /**
     * Get the list of the {@link FailureCause}'s names and ids. The list should be the latest possible from the DB as
     * they will be used for editing. The objects returned should contain at least the id and the name of the cause.
     *
     * @return the full list of the names and ids of the causes.
     */
    public abstract Collection<FailureCause> getCauseNames();

    /**
     * Get the cause with the given id. The cause returned is intended to be edited right away, so it should be as fresh
     * from the db as possible.
     *
     * @param id the id of the cause.
     * @return the cause or null if a cause with that id could not be found.
     */
    public abstract FailureCause getCause(String id);

    /**
     * Saves a new cause to the db and generates a new id for the cause.
     *
     * @param cause the cause to add.
     * @return the same cause but with a new id.
     */
    public abstract FailureCause addCause(FailureCause cause);

    /**
     * Saves a cause to the db. Assumes that the id is kept from when it was fetched. Can also be an existing cause in
     * another {@link KnowledgeBase} implementation with a preexisting id that is being converted via {@link
     * #convertFrom(KnowledgeBase)}.
     *
     * @param cause the cause to add.
     * @return the same cause but with a new id.
     */
    public abstract FailureCause saveCause(FailureCause cause);

    /**
     * Converts the existing old knowledge base into this one. Will be called after the creation of a new object when
     * then Jenkins config is saved, So it could just be that the old one is exactly the same as this one.
     *
     * @param oldKnowledgeBase the old one.
     */
    public abstract void convertFrom(KnowledgeBase oldKnowledgeBase);

    /**
     * Does a full copy of the data in the old one to this one. Using the public api, can be used by implementations of
     * {@link #convertFrom(KnowledgeBase)} to handle conversion from an unknown source type.
     *
     * @param oldKnowledgeBase the old one.
     * @see #convertFrom(KnowledgeBase)
     */
    protected void convertFromAbstract(KnowledgeBase oldKnowledgeBase) {
        for (FailureCause cause : oldKnowledgeBase.getCauseNames()) {
            saveCause(oldKnowledgeBase.getCause(cause.getId()));
        }
    }

    /**
     * Called to see if the configuration has changed.
     *
     * @param oldKnowledgeBase the previous config.
     * @return true if it is the same.
     */
    public abstract boolean equals(KnowledgeBase oldKnowledgeBase);

    @Override
    public int hashCode() {
        //Making checkstyle happy.
        return super.hashCode();
    }

    /**
     * Descriptor for {@link KnowledgeBase}s.
     */
    public abstract static class KnowledgeBaseDescriptor extends Descriptor<KnowledgeBase> {

        /**
         * All registered {@link KnowledgeBaseDescriptor}s.
         *
         * @return the extension list.
         */
        public static ExtensionList<KnowledgeBaseDescriptor> all() {
            return Jenkins.getInstance().getExtensionList(KnowledgeBaseDescriptor.class);
        }
    }
}
