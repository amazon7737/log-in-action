package org.fileupload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fileupload.domain.Item;
import org.fileupload.domain.ItemForm;
import org.fileupload.domain.UploadFile;
import org.fileupload.repository.ItemRepository;
//import org.fileupload.util.FileStore;
import org.fileupload.util.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {


     private final FileStore fileStore;

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(){
        return "item-form";
    }

    /**
     * V3 FileStore 클래스를 만들어서 작동
     */
    @PostMapping("/upload")
    public String saveItem(@ModelAttribute ItemForm form) throws IOException {

        UploadFile atachFile = fileStore.storeFile(form.getAttachFile());     // 하나 전달되는 일반파일 저장
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles()); // 여러개 전달되는 이미지 파일들 저장
        // Inline Variable (리팩토링) 단축키 Ctrl+Alt+N

        //위의까지 진행하면 폴더에 파일 저장까지는 된다, 이제 데이터베이스 (Map)에 저장한다.
        //데이터베이스에는 실제 파일들이 저장되는것이 아닌, (실제파일명,저장된파일명)에 대한 정보를 데이터베이스에 저장
        // (이미지파일은 S3에 올리고 URL링크를 데이터베이스에 저장하는것처럼 데이터베이스에는 실제 파일자체를 저장하지않는다.)
        // 경로또한 fullPath를 저장하진않고, 지정된 Path 그 이후의 경로만 저장한다고 한다.
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(atachFile);
        item.setImageFiles(storeImageFiles);
//        itemRepository.save(item);

//        redirectAttributes.addAttribute("itemId", item.getId());

//        return "redirect:/items/{itemId}";
        return "item-form";
    }

    /**
     * V2
     */
    @PostMapping("/upload/v2")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file,
                           HttpServletRequest request) throws IOException {
        log.info("request={}", request);
        log.info("itemName={}", itemName);
        log.info("multipartFile={}", file);

        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath));

        }

        return "item-form";
    }

    /**
     * 스프링 기능 없이
     */
//    @PostMapping("/upload")
//    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
//        log.info("request={}", request);
//
//        String itemName = request.getParameter("itemName");
//        log.info("itemName ={}", itemName);
//
//        Collection<Part> parts = request.getParts();
//        log.info("parts ={}", parts);
//
//        for (Part part : parts) {
//            log.info("==== PART ====");
//            log.info("name={}", part.getName());
//            Collection<String> headerNames = part.getHeaderNames();
//            for (String headerName : headerNames) {
//                log.info("header {}: {}", headerName, part.getHeader(headerName));
//            }
//            // 편의 메서드
//            // content-disposition; filename
//            log.info("submittedFilename={}", part.getSubmittedFileName());
//            log.info("size={}", part.getSize()); // part body size
//
//            //데이터 읽기
//            InputStream inputStream = part.getInputStream();
//            // 바이너리 데이터를 문자로 바꿀 경우에는 CharSet을 설정해주자!
//            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//            log.info("body={}", body);
//
//            // 파일 저장하기
//            if (StringUtils.hasText(part.getSubmittedFileName())) {
//                String fullPath = fileDir + part.getSubmittedFileName();
//                log.info("파일 저장 fullPath={}", fullPath);
//                part.write(fullPath);
//            }
//
//            inputStream.close();
//        }

//        return "item-form";
//    }


}
