package com.java.community.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import cn.ucloud.ufile.http.OnProgressListener;
import com.java.community.exception.CustomizeErrorCode;
import com.java.community.exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

/**
 * Author: yk
 * Date: 2020/5/18 17:46
 */
@Component
public class UCloudProvider {

    //配置公钥、私钥和存储空间名
    // 对应application.properties中自定义的...
    @Value("${ucloud.ufile.public-key}")
    private String publicKey;//公钥
    @Value("${ucloud.ufile.private-key}")
    private String privateKey;//私钥
    @Value("${ucloud.ufile.bucket-name}")
    private String bucketName;//存储空间名
    @Value("${ucloud.ufile.region}")
    private String region;//地域
    @Value("${ucloud.ufile.proxy-suffix}")
    private String proxySuffix;//后缀
    @Value("${ucloud.ufile.expires-duration}")
    private Integer expiresDuration;//有效时间

    //同步上传文件
    public String upload(InputStream fileStream, String mimeType, String fileName){
        String generatedFileName = "";
        String[] filePath = fileName.split("\\.");
        if (filePath.length > 1){
            //生成文件名
            generatedFileName = UUID.randomUUID().toString().replaceAll("-","") + "." +filePath[filePath.length-1];
        } else {
            return null;
        }

        try {
            // 对象相关API的授权器
            ObjectAuthorization objectAuthorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
            // 对象操作需要ObjectConfig来配置您的地区和域名后缀
            ObjectConfig config = new ObjectConfig(region, proxySuffix);

            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(fileStream, mimeType)
                    .nameAs(generatedFileName)  //上传文件名
                    .toBucket(bucketName)  //存储空间名
                    .setOnProgressListener((bytesWritten, contentLength) -> {
                    })
                    .execute();

            if (response != null && response.getRetCode() == 0){
                String url = UfileClient.object(objectAuthorization, config)
                        .getDownloadUrlFromPrivateBucket(generatedFileName, bucketName, 365*24*60*60*10)
                        .createUrl();
                return url;
            } else {
                throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
            }
        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
    }



}
