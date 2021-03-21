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

import java.util.*;

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


    /**
     * 获取的使商品分类以及子分类的数据
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        if (categoryId == null){
            return ServerResponse.createByErrorMessage("分类id不能为空");
        }
        HashSet<Category> categoryHashSet = Sets.newHashSet();
        findChildCategory(categoryHashSet,categoryId);
        List<Integer> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(categoryHashSet)){
            for (Category category : categoryHashSet) {
                list.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     *
     * @param categorySet 分类集合
     * @param categoryId    分类id
     * 递归查询分类
     */
    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (categoryId != null){
            categorySet.add(category);
        }
        //查询子节点
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isNotEmpty(categoryList)){
            for (Category categoryItem : categoryList) {
                findChildCategory(categorySet,categoryItem.getId());
            }
        }
    }


}
