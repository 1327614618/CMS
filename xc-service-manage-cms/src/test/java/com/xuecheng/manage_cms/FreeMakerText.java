package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FreeMakerText {

    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testGridFs() throws FileNotFoundException {
        //要存储的文件
        File file = new File("D:/index_banner.html");
        //定义输入流
        FileInputStream inputStram = null ;

        try {
            inputStram = new FileInputStream(file);
        //向GridFS存储文件
            ObjectId objectId = gridFsTemplate.store(inputStram, "轮播图测试文件01", "");
        //得到文件ID
            String fileId = objectId.toString();
            System.out.println(file);
            System.out.println(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void queryFile() throws IOException {
        String fileId = "5e06b775d43fa819ecd7b425";
        //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream;
        try {
        gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
            String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRestTemplate() {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(forEntity);
    }

    @RequestMapping("/banner")
    public String index_banner(Map<String, Object> map) {
        String dataUrl = "http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f";
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }
}
