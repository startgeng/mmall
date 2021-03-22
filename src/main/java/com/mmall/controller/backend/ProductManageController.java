package com.mmall.controller.backend;


import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * Created by kevin
 * @author chenligeng
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     *
     * @return 商品列表接口
     */
    @RequestMapping(value = "/list.do")
    @ResponseBody
    public ServerResponse<?> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆，请先登陆在查看");
        }
        //校验角色是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //进行业务操作
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员,请联系开发人员，进行操作");
        }
    }

    /**
     * 产品搜索
     * @param productName 商品名称
     * @param productId 商品id
     * @param pageNum  当前页数
     * @param pageSize  每页显示条数
     * @return  返回一个对象的集合
     */
    @RequestMapping(value = "/search.do")
    @ResponseBody
    public ServerResponse<?> search(String productName,Integer productId,
                                    @RequestParam(value = "pageNum")int pageNum,
                                    @RequestParam(value = "pageSize")int pageSize,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆,请先登陆用户");
        }
        //校验是否是管理员用户
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务处理
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员,请联系开发人员，进行操作");
        }
    }

    /**
     * 图片上传
     * @param session session
     * @param file 文件上传
     * @param request request请求
     * @return 图片上传
     */
    @RequestMapping(value = "/upload.do")
    @ResponseBody
    public ServerResponse<?> upload(HttpSession session,
                                    @RequestParam(value = "upload_file",required = false)MultipartFile file,
                                    HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆，请先登陆");
        }
        //检查是否是管理员用户
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务处理
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix" + targetFileName);
            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("uri",targetFileName);
            map.put("url",url);
            return ServerResponse.createBySuccess(map);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员,请联系开发人员，进行操作");
        }
    }

    /**
     * 根据商品id查询商品
     * @param productId 商品id
     * @return 商品详情
     */
    @RequestMapping(value = "/detail.do")
    @ResponseBody
    public ServerResponse<?> detail(@RequestParam(value = "productId")Integer productId,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆，请先登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务操作
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员,请联系开发人员，进行操作");
        }
    }

    /**
     * 更新商品状态
     * @param productId 商品id
     * @param status 商品状态
     * @return 更新商品状态
     */
    @RequestMapping(value = "/set_sale_status.do")
    @ResponseBody
    public ServerResponse<?> setSaleStatus(@RequestParam(value = "productId")Integer productId,@RequestParam(value = "status")Integer status, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登陆，请先登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务操作
            return iProductService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员,请联系开发人员，进行操作");
        }
    }
}
