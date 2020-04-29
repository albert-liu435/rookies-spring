package org.springframework.core.io.support;

import org.junit.Test;
import org.springframework.core.io.Resource;

import java.net.URL;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/29 21:02
 */
public class PathMatchingResourcePatternResolverTest {

    PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Test
    public void testfindPathMatchingResources() throws Exception {

        Resource[] resource = pathMatchingResourcePatternResolver.getResources("classpath*:/config/*.txt");

        for (int i = 0; i < resource.length; i++) {
            String filename = resource[i].getFilename();
            URL url = resource[i].getURL();
            System.out.println(url);
        }
        // System.out.println(resource);
    }

}
