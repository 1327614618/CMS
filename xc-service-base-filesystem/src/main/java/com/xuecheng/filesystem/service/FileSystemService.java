package com.xuecheng.filesystem.service;

import com.xuecheng.filesystem.dao.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemService {

    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    String  connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    String  network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String  charset;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String  tracker_servers;

    @Autowired
    FileSystemRepository fileSystemRepository;

    //上传文件到fdfs，返回文件id
   public String file_upload(MultipartFile file){
         return null;
   }


}
