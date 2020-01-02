package com.xuecheng.manage_course.service;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import com.xuecheng.manage_course.dao.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryNode findCategoryList() {

        CategoryNode categoryNode = categoryMapper.selectList();
        
        return categoryNode;
    }
}
