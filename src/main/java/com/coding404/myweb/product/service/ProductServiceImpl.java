package com.coding404.myweb.product.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coding404.myweb.command.CategoryVO;
import com.coding404.myweb.command.ProductUploadVO;
import com.coding404.myweb.command.ProductVO;
import com.coding404.myweb.util.Criteria;

@Service("productService")
public class ProductServiceImpl implements ProductService{
   
   @Autowired
   private ProductMapper productMapper;
   
   ///////////////////////////////////////
   @Value("${project.upload.path}")
   private String uploadPath;

   //폴더 생성 함수
   public String makeFolder() {

      String path = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      File file = new File(uploadPath+"/"+path);
      if(file.exists() == false) { // 존재하면 true, 존재하지 않으면 false
         file.mkdir();
      }

      return path; // 날짜 폴더명 반환
   }
   ////////////////////////////////////////
   //하나의 메서드에서 여러CRUD작업이 일어나는 경우에 한부분이 에러가 발생하면 그 에러를 처리하고, 롤백처리를 대신합니다.
   @Override
   @Transactional(rollbackFor = Exception.class)
   public int productRegist(ProductVO vo,List<MultipartFile> list) {
      //product테이블 처리
      int result = productMapper.productRegist(vo);      
      
      //업로드처리
      for(MultipartFile file : list) {

         System.out.println(file.isEmpty());

         //파일이름을 받습니다.
         String originName = file.getOriginalFilename();
         //브라우저 별로 파일의 경로가 다를수 있기 때문에 \\ 기준으로 파일명만 잘라서 다시 저장
         String filename = originName.substring(originName.lastIndexOf("\\")+1);   
         //동일한 파일을 재업로드시 기존파일 덮어버리기 때문에, 난수 이름으로 파일명을 바꿔서 올림
         String uuid = UUID.randomUUID().toString();   
         //날짜별로 폴더생성   
         String filepath = makeFolder();   
         //세이브할 경로
         String savepath = uploadPath+"/"+filepath+"/"+uuid+"_"+filename;

         //System.out.println(originName);
         //System.out.println(size);
         //데이터베이스 추후에 저장
         System.out.println("실제파일명:"+filename);
         System.out.println(uploadPath);
         System.out.println("날짜폴더경로 : "+filepath);
         System.out.println("세이브 할 경로:" + savepath);
         System.out.println("UUID 난수값 = "+uuid);
         System.out.println("=========================");
         try {
            File saveFile = new File(savepath);
            file.transferTo(saveFile); // 파일 업로드를 진행
         } catch (Exception e) {
            System.out.println("파일업로드 중 error 발생");
            e.printStackTrace();
            return 0; // 실패의 의미
         }
         
         //productUpload테이블에 파일의 경로 인서트
         //prod_id는 insert전에 selectKey를 통해서 얻습니다.
         productMapper.productFileRegist(ProductUploadVO.builder()
                                             .filename(filename)
                                             .filepath(filepath)
                                             .uuid(uuid)
                                             .prod_writer(vo.getProd_writer())                                          
                                             .build());
         
         
      }//end for문
      
      
      
      
      return result;
   }


   @Override
   public ArrayList<ProductVO> getList(String writer,Criteria cri) {
      
      return productMapper.getList(writer,cri);
   }
   

   @Override
   public int getTotal(String writer,Criteria cri) {
      return productMapper.getTotal(writer,cri);
   }

   @Override
   public ProductVO getDetail(int prod_id) {
      
      return productMapper.getDetail(prod_id);
   }


   @Override
   public int productUpdate(ProductVO vo) {
      
      return productMapper.productUpdate(vo);
   }


   @Override
   public int productDelete(int prod_id) {
      
      return productMapper.productDelete(prod_id);
   }


   @Override
   public ArrayList<CategoryVO> getCategory() {
      return productMapper.getCategory();
   }


   @Override
   public ArrayList<CategoryVO> getCategoryChild(CategoryVO vo) {
      return productMapper.getCategoryChild(vo);
   }
@Override
public ArrayList<ProductUploadVO> getAjaxImg(int prod_id) {
	// TODO Auto-generated method stub
	return productMapper.getAjaxImg(prod_id);
}




   
   
}