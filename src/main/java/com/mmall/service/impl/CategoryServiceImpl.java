package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by geely
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     *  添加商品分类
     * @param categoryName  商品分类名称
     * @param parentId  商品父分类id
     * @return  返回一个通用返回对象
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isBlank(categoryName) || parentId == null){
            return ServerResponse.createByErrorMessage("添加商品分类错误");
        }
        //封装一个对象
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        category.setCreateTime(new Date());
        //插入节点
        int resultCount = categoryMapper.insert(category);
        //判断节点是否插入成功
        if (resultCount > 0){
            //成功
            return ServerResponse.createBySuccessMessage("添加商品分类成功");
        }
        //失败
        return ServerResponse.createByErrorMessage("添加商品分类失败");
    }

//    @Override
//    public ServerResponse updateCategoryName(Integer categoryId, String categoryName){
//        if(categoryId == null || StringUtils.isBlank(categoryName)){
//            return ServerResponse.createByErrorMessage("更新品类参数错误");
//        }
//        Category category = new Category();
//        category.setId(categoryId);
//        category.setName(categoryName);
//
//        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
//        if(rowCount > 0){
//            return ServerResponse.createBySuccess("更新品类名字成功");
//        }
//        return ServerResponse.createByErrorMessage("更新品类名字失败");
//    }

    /**
     * 更新商品分类名称
     * @param categoryId 商品分类id
     * @param categoryName 商品分类名称
     * @return
     */
    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName) || categoryId ==null){
            return ServerResponse.createByErrorMessage("更新商品参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新品类");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    /**
     * 获取当前节点和递归获取子节点的值
     * @param categoryId 分类id
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isNotEmpty(categoryList)){
            return ServerResponse.createByErrorMessage("未能找到当前商品的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }




    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
//    @Override
//    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
//        Set<Category> categorySet = Sets.newHashSet();
//        findChildCategory(categorySet,categoryId);
//
//
//        List<Integer> categoryIdList = Lists.newArrayList();
//        if(categoryId != null){
//            for(Category categoryItem : categorySet){
//                categoryIdList.add(categoryItem.getId());
//            }
//        }
//        return ServerResponse.createBySuccess(categoryIdList);
//    }



    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> list = Lists.newArrayList();
        if (categoryId != null || CollectionUtils.isNotEmpty(categorySet)){
            return ServerResponse.createByErrorMessage("商品分类的id为空和他的子分类为空");
        }
        for (Category category : categorySet) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);
    }

    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isNotEmpty(categoryList)){
            for (Category categoryItem : categoryList) {
                findChildCategory(categorySet,categoryItem.getId());
            }
        }
    }

    /**
     * 递归查询出子节点
     * @param categorySet
     * @param categoryId
     */
//    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if (category != null){
//            categorySet.add(category);
//        }
//        //查询出子节点  select * from category where parent_Id = #{parent_Id}
//        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        for (Category categoryItem : categoryList) {
//            findChildCategory(categorySet,categoryItem.getId());
//        }
//    }


    //递归算法,算出子节点
//    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if(category != null){
//            categorySet.add(category);
//        }
//        //查找子节点,递归算法一定要有一个退出的条件
//        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        for(Category categoryItem : categoryList){
//            findChildCategory(categorySet,categoryItem.getId());
//        }
//        return categorySet;
//    }






}
