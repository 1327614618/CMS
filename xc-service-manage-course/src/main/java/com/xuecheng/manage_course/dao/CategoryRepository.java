package com.xuecheng.manage_course.dao;


import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository {
    public List<CategoryNode> findCategoryList();

}
