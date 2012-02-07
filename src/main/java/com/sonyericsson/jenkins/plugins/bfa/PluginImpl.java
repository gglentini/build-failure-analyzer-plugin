/*
 * The MIT License
 *
 * Copyright 2012 Sony Ericsson Mobile Communications. All rights reserved.
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

package com.sonyericsson.jenkins.plugins.bfa;

import com.sonyericsson.jenkins.plugins.bfa.model.FailureCause;
import hudson.Plugin;
import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Hudson;
import hudson.security.Permission;
import hudson.security.PermissionGroup;
import hudson.util.CopyOnWriteList;

import java.util.Collection;

/**
 * The main thing.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class PluginImpl extends Plugin {

    /**
     * The permission group for all permissions related to this plugin.
     */
    public static final PermissionGroup PERMISSION_GROUP =
            new PermissionGroup(PluginImpl.class, Messages._PermissionGroup_Title());

    /**
     * Permission to update the causes. E.e. Access {@link CauseManagement}.
     */
    public static final Permission UPDATE_PERMISSION =
            new Permission(PERMISSION_GROUP, "UpdateCauses",
                    Messages._PermissionUpdate_Description(), Hudson.ADMINISTER);

    private static String staticResourcesBase = null;

    private CopyOnWriteList<FailureCause> causes = new CopyOnWriteList<FailureCause>();

    @Override
    public void start() throws Exception {
        super.start();
        load();
    }

    /**
     * Returns the base relative URI for static resources packaged in webapp.
     *
     * @return the base URI.
     */
    public static String getStaticResourcesBase() {
        if (staticResourcesBase == null) {
            PluginManager pluginManager = Hudson.getInstance().getPluginManager();
            if (pluginManager != null) {
                PluginWrapper wrapper = pluginManager.getPlugin(PluginImpl.class);
                if (wrapper != null) {
                    staticResourcesBase = "/plugin/" + wrapper.getShortName();
                }
            }
            //Did we really find it?
            if (staticResourcesBase == null) {
                //This is not the preferred way since the module name could change,
                //But in some unit test cases we cannot reach the plug-in info.
                return "/plugin/build-failure-analyzer";
            }
        }
        return staticResourcesBase;
    }

    /**
     * Returns the base relative URI for static images packaged in webapp.
     *
     * @return the images directory.
     *
     * @see #getStaticResourcesBase()
     */
    public static String getStaticImagesBase() {
        return getStaticResourcesBase() + "/images";
    }

    /**
     * Provides a Jenkins relative url to a plugin internal image.
     *
     * @param size the size of the image (the sub directory of images).
     * @param name the name of the image file.
     * @return a URL to the image.
     */
    public static String getImageUrl(String size, String name) {
        return getStaticImagesBase() + "/" + size + "/" + name;
    }

    /**
     * Returns the singleton instance.
     *
     * @return the one.
     */
    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }

    /**
     * A direct reference to the list of Failure Causes.
     *
     * @return the causes.
     */
    public CopyOnWriteList<FailureCause> getCauses() {
        return causes;
    }

    /**
     * Sets the list of failure causes to the provided list. Only the content of the provided list will be used not the
     * list itself.
     *
     * @param causes the list.
     * @see CopyOnWriteList#replaceBy(hudson.util.CopyOnWriteList).
     */
    public void setCauses(CopyOnWriteList<FailureCause> causes) {
        this.causes.replaceBy(causes);
    }

    /**
     * Sets the list of failure causes to the provided list. Only the content of the provided list will be used not the
     * list itself.
     *
     * @param causes the list.
     * @see CopyOnWriteList#replaceBy(java.util.Collection)
     */
    public void setCauses(Collection<FailureCause> causes) {
        this.causes.replaceBy(causes);
    }
}
