package org.nd4j.resources;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.resources.strumpf.ResourceFile;
import org.nd4j.resources.strumpf.StrumpfResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStrumpf {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testResourceFiles() throws Exception {
//        File cacheDir = testDir.newFolder();
        File cacheDir = new File("C:\\Temp\\TestResources");


        File f = new File("C:\\DL4J\\Git\\dl4j-test-resources\\src\\main\\resources\\strumpf\\gauss-vector.txt.resource_reference");

        ResourceFile rf = ResourceFile.fromFile(f);
        File lf = rf.localFile(cacheDir);

        assertTrue(lf.exists());

        try(Reader r = new BufferedReader(new FileReader(lf))){
            LineIterator iter = IOUtils.lineIterator(r);
            for( int i=0; i<5 && iter.hasNext(); i++ ){
                System.out.println("LINE " + i + ": " + iter.next());
            }
        }

    }

    @Test
    public void testResolvingReference() throws Exception {

        File f = Resources.asFile("strumpf/gauss-vector.txt");
        assertTrue(f.exists());

        System.out.println(f.getAbsolutePath());
        try(Reader r = new BufferedReader(new FileReader(f))){
            LineIterator iter = IOUtils.lineIterator(r);
            for( int i=0; i<5 && iter.hasNext(); i++ ){
                System.out.println("LINE " + i + ": " + iter.next());
            }
        }
    }

    @Test
    public void testResolvingActual() throws Exception {
        File f = Resources.asFile("data/irisSvmLight.txt");
        assertTrue(f.exists());

        //System.out.println(f.getAbsolutePath());
        int count = 0;
        try(Reader r = new BufferedReader(new FileReader(f))){
            LineIterator iter = IOUtils.lineIterator(r);
            while(iter.hasNext()){
                String line = iter.next();
                //System.out.println("LINE " + i + ": " + line);
                count++;
            }
        }

        assertEquals(12, count);        //Iris normally has 150 examples; this is subset with 12
    }

    @Test
    public void testResolveLocal() throws Exception {

        File dir = testDir.newFolder();

        String content = "test file content";
        String path = "myDir/myTestFile.txt";
        File testFile = new File(dir, path);
        testFile.getParentFile().mkdir();
        FileUtils.writeStringToFile(testFile, content, StandardCharsets.UTF_8);

        System.setProperty(StrumpfResolver.LOCAL_DIRS_SYSTEM_PROPERTY, dir.getAbsolutePath());

        try{
            StrumpfResolver r = new StrumpfResolver();
            assertTrue(r.exists(path));
            File f = r.asFile(path);
            assertTrue(f.exists());
            assertEquals(testFile.getAbsolutePath(), f.getAbsolutePath());
            String s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
            assertEquals(content, s);
        } finally {
            System.setProperty(StrumpfResolver.LOCAL_DIRS_SYSTEM_PROPERTY, "");
        }
    }

}
