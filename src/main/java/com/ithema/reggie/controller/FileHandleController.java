package com.ithema.reggie.controller;

import com.ithema.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 处理文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class FileHandleController {

    @Value("${reggie.path}")
    private String path;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是临时文件，如果不处理就会被删除。
        //log.info(file.toString());
        //String originalFilename=file.getOriginalFilename();
        //使用uuid随机生成文件名
        String suffix= file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filename= UUID.randomUUID().toString()+suffix;
        log.info("当前上传的文件名是{}",filename);

        //创建一个目录文件
        File dir=new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }

        try{
            file.transferTo(new File(path+filename));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(filename);
    }

    /**
     * 文件下载，通过输出流将图片写回前端
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(new File(path+name));
        ServletOutputStream outputStream=response.getOutputStream();
        response.setContentType("image/jpeg");

        int len=0;
        byte[] bytes=new byte[1024];
        while ((len= fileInputStream.read(bytes))!=-1)
        {
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();

    }


}
