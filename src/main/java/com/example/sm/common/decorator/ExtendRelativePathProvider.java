package com.example.sm.common.decorator;

import springfox.documentation.spring.web.paths.AbstractPathProvider;
public class ExtendRelativePathProvider extends AbstractPathProvider {

    public static final String ROOT = "";
    @Override
    protected String applicationPath() {
        return ROOT;
    }
    public ExtendRelativePathProvider() {
        System.out.println("Loading The Path Resolver");
    }
    @Override
    protected String getDocumentationPath() {
        return "/api/docs";
    }
}