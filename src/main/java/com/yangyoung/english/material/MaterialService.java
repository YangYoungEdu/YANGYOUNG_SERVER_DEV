package com.yangyoung.english.material;

import org.springframework.stereotype.Service;

@Service
public class MaterialService {
//    public String uploadFile(MultipartFile file) throws IOException {
//
//        String sid = authenticate();
//        if (sid == null) {
//            throw new RuntimeException("Failed to authenticate with Synology");
//        }
//
//        String uploadUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2&_sid=" + sid;
//
//        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
//            HttpPost httppost = new HttpPost(uploadUrl);
//
//            File tempFile = File.createTempFile("upload", file.getOriginalFilename());
//            file.transferTo(tempFile);
//
//            FileBody fileBody = new FileBody(tempFile, ContentType.DEFAULT_BINARY, file.getOriginalFilename());
//            System.out.println(fileBody.getFilename());
//
//            HttpEntity reqEntity = MultipartEntityBuilder.create()
//                    .addPart("path", new StringBody("/YangYoung/test", ContentType.TEXT_PLAIN))
//                    .addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
//                    .addPart("filename", fileBody)
//                    .setLaxMode()
//                    .build();
//
//            httppost.setEntity(reqEntity);
//
//            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
//                HttpEntity resEntity = response.getEntity();
//                if (resEntity != null) {
//                    String responseString = EntityUtils.toString(resEntity);
//                }
//                EntityUtils.consume(resEntity);
//            }
//        }
//
//        return "success";
//    }
}
