package com.coding404.myweb.product.service;

import java.util.ArrayList;

import com.coding404.myweb.command.ProductVO;

import com.coding404.myweb.util.Criteria;

public interface ProductService {
   
   public int productRegist(ProductVO vo);
   public ArrayList<ProductVO> getList(String writer,Criteria cri);
   public int getTotal(String writer,Criteria cri);
   public ProductVO getDetail(int prod_id);
   public int productUpdate(ProductVO vo);
   public int productDelete(int prod_id);


}