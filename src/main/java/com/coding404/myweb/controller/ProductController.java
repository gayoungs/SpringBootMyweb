package com.coding404.myweb.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coding404.myweb.command.ProductVO;
import com.coding404.myweb.product.service.ProductService;
import com.coding404.myweb.util.Criteria;
import com.coding404.myweb.util.PageVO;

@Controller
@RequestMapping("/product")
public class ProductController {
   
   @Autowired
   @Qualifier("productService")
   private ProductService productService;
   
   @GetMapping("/productList")
   public String list(Model model,Criteria cri) {
      
      //로그인 기능이 없으므로, admin이라는 계정기반으로 조회
      String writer = "admin";
//      ArrayList<ProductVO> list = productService.getList(writer);
//      model.addAttribute("list", list);
      ArrayList<ProductVO> list = productService.getList(writer,cri);
      
      int total = productService.getTotal(writer,cri);
      PageVO pageVO = new PageVO(cri,total);
      
      model.addAttribute("list", list);
      model.addAttribute("pageVO", pageVO);
      System.out.println(pageVO.toString());
      return "product/productList";
   } 
   
   @GetMapping("/productReg")
   public String reg() {
      return "product/productReg";
   } 
   
   
   @GetMapping("/productDetail")
   public String detail(@RequestParam("prod_id") int prod_id,
                    Model model) {
      
      //조회 ~ prod_id가 필요
      ProductVO vo = productService.getDetail(prod_id);
      model.addAttribute("vo", vo);
      return "product/productDetail";
   } 
   
   //post방식
   //등록요청
   @PostMapping("/registForm")
   public String registForm(ProductVO vo, RedirectAttributes ra,
		   					@RequestParam("file") List<MultipartFile> list //업로드
		   					) {
	   
	   //1. 빈객체검사
	   list = list.stream().filter( t -> t.isEmpty() == false ).collect(Collectors.toList());
      
	   //2.확장자검사
	   for( MultipartFile file : list ) {
		   //System.out.println(file.getContentType());
		   //file.getCintentType();
		   
		   if(file.getContentType().contains("image")==false) {
			   ra.addFlashAttribute("msg","jpg,jpeg,png 이미지 파일만 등록이 가능합니다");
			   return "redirect:/product/productList"; //이미지가 아니라면 list목록으로
		   }
	   }
	   
	   //3. 파일을 처리하는 작업은 service위임 => 애시당초에  controller Request 객체를 받고 service 위임전략
	   
	   
	   
//	   System.out.println(vo.toString());
	   
      int result = productService.productRegist(vo,list);
      String msg = result == 1 ? "등록 되었습니다" : "등록 실패";
      ra.addFlashAttribute("msg",msg);
      
      return "redirect:/product/productList";
   }
   
   @PostMapping("/modifyForm")
   public String modifyForm(ProductVO vo, RedirectAttributes ra) {
      int result = productService.productUpdate(vo);
      System.out.println(result);
      String msg = result==1? "업데이트 성공하셨어요 고갱님" : "업데이트 실패하셨어요 고갱님";
      ra.addFlashAttribute("msg2", msg);
      return "redirect:/product/productList";
   }
   
   
   @PostMapping("/deleteForm")
   public String deleteForm(ProductVO vo, RedirectAttributes ra) {
      int result = productService.productDelete(vo.getProd_id());
      String msg = result==1?"삭제 성공":"삭제 실패";
      ra.addFlashAttribute("msg3", msg);
      return "redirect:/product/productList";
   }
   
   
   
   
   
   
   
   
   
}