/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.patch.management;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import io.fabric8.patch.management.impl.GitPatchManagementService;
import io.fabric8.patch.management.impl.GitPatchManagementServiceImpl;
import io.fabric8.patch.management.impl.GitPatchRepository;
import io.fabric8.patch.management.impl.GitPatchRepositoryImpl;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.startlevel.BundleStartLevel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GitPatchManagementServiceTest extends PatchTestSupport {

    private GitPatchManagementService pm;
    private BundleStartLevel bsl;

    private GitPatchRepositoryImpl repository;

    @Before
    public void init() throws IOException {
        super.init();

        bsl = mock(BundleStartLevel.class);
        when(bundle.adapt(BundleStartLevel.class)).thenReturn(bsl);

        when(systemContext.getDataFile("patches")).thenReturn(new File(karafHome, "data/cache/bundle0/data/patches"));
    }

    @Test
    public void disabledPatchManagement() {
        properties.remove("fuse.patch.location");
        pm = new GitPatchManagementServiceImpl(bundleContext);
        pm.start();
        assertFalse(pm.isEnabled());
    }

    @Test
    public void enabledPatchManagement() {
        pm = new GitPatchManagementServiceImpl(bundleContext);
        pm.start();
        assertTrue(pm.isEnabled());
    }

    @Test
    public void initializationPerformedNoFuseVersion() {
        pm = new GitPatchManagementServiceImpl(bundleContext);
        pm.start();
        try {
            pm.ensurePatchManagementInitialized();
            fail("Should fail, because versions can't be determined");
        } catch (PatchException e) {
            assertTrue(e.getMessage().contains("Can't determine Fuse/Fabric8 version"));
        }
    }

    @Test
    public void initializationPerformedNoBaselineDistribution() throws IOException {
        setVersions();
        pm = new GitPatchManagementServiceImpl(bundleContext);
        pm.start();
        try {
            pm.ensurePatchManagementInitialized();
            fail("Should fail, because no baseline distribution is found");
        } catch (PatchException e) {
            assertTrue(e.getMessage().contains("Can't find baseline distribution"));
        }
    }

    @Test
    public void initializationPerformedBaselineDistributionFoundInPatches() throws IOException, GitAPIException {
        setVersions();
        freshKarafDistro();
        preparePatchZip("src/test/resources/baselines/baseline1", "target/karaf/patches/jboss-fuse-full-6.2.0-baseline.zip", true);
        validateInitialGitRepository();
    }

    @Test
    public void initializationPerformedBaselineDistributionFoundInSystem() throws IOException, GitAPIException {
        setVersions();
        freshKarafDistro();
        preparePatchZip("src/test/resources/baselines/baseline1", "target/karaf/system/org/jboss/fuse/jboss-fuse-full/6.2.0/jboss-fuse-full-6.2.0-baseline.zip", true);
        validateInitialGitRepository();
    }

    @Test
    public void initializationPerformedPatchManagementAlreadyInstalled() throws IOException, GitAPIException {
        testWithAlreadyInstalledPatchManagementBundle("1.2.0");
    }

    @Test
    public void initializationPerformedPatchManagementInstalledAtOlderVersion() throws IOException, GitAPIException {
        testWithAlreadyInstalledPatchManagementBundle("1.1.9");
    }

    private void testWithAlreadyInstalledPatchManagementBundle(String version) throws IOException, GitAPIException {
        setVersions();
        freshKarafDistro();
        String line = String.format("io/fabric8/patch/patch-management/%s/patch-management-%s.jar=2\n", version, version);
        FileUtils.write(new File(karafHome, "etc/startup.properties"), line, true);
        preparePatchZip("src/test/resources/baselines/baseline1", "target/karaf/system/org/jboss/fuse/jboss-fuse-full/6.2.0/jboss-fuse-full-6.2.0-baseline.zip", true);
        validateInitialGitRepository();
    }

    @Test
    public void addPatch4() throws IOException, GitAPIException {
        initializationPerformedBaselineDistributionFoundInSystem();

        // prepare some ZIP patches
        preparePatchZip("src/test/resources/content/patch4", "target/karaf/patches/source/patch-4.zip", false);

        PatchManagement service = (PatchManagement) pm;
        PatchData patchData = service.fetchPatches(new File("target/karaf/patches/source/patch-4.zip").toURI().toURL()).get(0);
        assertThat(patchData.getId(), equalTo("patch-4"));
        service.trackPatch(patchData);
    }

    private void validateInitialGitRepository() throws IOException, GitAPIException {
        pm = new GitPatchManagementServiceImpl(bundleContext);
        pm.start();
        pm.ensurePatchManagementInitialized();
        GitPatchRepository repository = ((GitPatchManagementServiceImpl) pm).getGitPatchRepository();

        verify(bsl).setStartLevel(2);

        Git fork = repository.cloneRepository(repository.findOrCreateMainGitRepository(), true);
        List<Ref> tags = fork.tagList().call();
        boolean found = false;
        for (Ref tag : tags) {
            if ("refs/tags/baseline-6.2.0".equals(tag.getName())) {
                found = true;
                break;
            }
        }
        assertTrue("Repository should contain baseline tag for version 6.2.0", found);

        // look in etc/startup.properties for installed patch-management bundle
        List<String> lines = FileUtils.readLines(new File(karafHome, "etc/startup.properties"));
        found = false;
        for (String line : lines) {
            if ("io/fabric8/patch/patch-management/1.1.9/patch-management-1.1.9.jar=2".equals(line)) {
                fail("Should not contain old patch-management bundle in etc/startup.properties");
            }
            if ("io/fabric8/patch/patch-management/1.2.0/patch-management-1.2.0.jar=2".equals(line)) {
                if (found) {
                    fail("Should contain only one declaration of patch-management bundle in etc/startup.properties");
                }
                found = true;
            }
        }
    }

    /**
     * Create crucial Karaf files (like etc/startup.properties)
     */
    private void freshKarafDistro() throws IOException {
        FileUtils.copyFile(new File("src/test/resources/karaf/etc/startup.properties"), new File(karafHome, "etc/startup.properties"));
        FileUtils.copyFile(new File("src/test/resources/karaf/bin/start"), new File(karafHome, "bin/start"));
        FileUtils.copyFile(new File("src/test/resources/karaf/bin/stop"), new File(karafHome, "bin/stop"));
        FileUtils.copyFile(new File("src/test/resources/karaf/lib/karaf.jar"), new File(karafHome, "lib/karaf.jar"));
        new File(karafHome, "licenses").mkdirs();
        new File(karafHome, "metatype").mkdirs();
    }

    private void setVersions() throws IOException {
        String versions = "fuse = 6.2.0\n" +
                "fabric = 1.2.0\n";
        FileUtils.write(new File(karafHome, "fabric/import/fabric/profiles/default.profile/io.fabric8.version.properties"), versions);
    }

}