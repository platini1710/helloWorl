package com.javahungry.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;



@RestController
public class HelloWorldController {
	@Autowired
	RestTemplate restTemplate;
	@Value("${proxy.url.base}")
	private String urlBase;
	private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

	@RequestMapping(path = "/saludo", method = RequestMethod.GET)
	public String sayHello() {
		return "Hello!! from Java Hungry";
	}

	@RequestMapping(value = "/uploadFile/{path}", method = RequestMethod.POST)
	public String uploadingFilePost(@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles,
			@PathVariable("path") String path, @RequestParam(name = "file") String file, HttpServletRequest request)
			throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		String url = request.getRequestURI();
		logger.info("url:::" + url);
		String host = request.getScheme();
		host = host + "://" + request.getServerName();
		host = host + ":" + request.getServerPort() + "/";
		logger.info("url :" + host);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		for (MultipartFile uploadedFile : uploadingFiles) {
			logger.info("nombre archivo:::" + uploadedFile.getOriginalFilename());
			final ByteArrayResource byteArrayResource = new ByteArrayResource(uploadedFile.getBytes()) {
				@Override
				public String getFilename() {
					return uploadedFile.getOriginalFilename();
				}
			};

			body.add("uploadingFiles", byteArrayResource);
		}
		String serverUrl = urlBase + "/uploadFile/" + path + "?file=" + file;
		logger.info("url:::" + serverUrl);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity,
				String.class);

		return response.getBody();

	}

	@RequestMapping(method = RequestMethod.GET, value = "/uploadFile/{path}", produces = "application/json")
	public @ResponseBody String listarFiles(@PathVariable("path") String path) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		return restTemplate.exchange(urlBase + "/uploadFile/" + path, HttpMethod.GET, entity, String.class).getBody();
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteFile/{path}", produces = "application/json")
	public @ResponseBody String borrarArchivos(@PathVariable("path") String path,
			@RequestParam(name = "file") String file) {
		logger.debug("urlBase:::" + urlBase);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		return restTemplate
				.exchange(urlBase + "/deleteFile/" + path + "?file=" + file, HttpMethod.DELETE, entity, String.class)
				.getBody();
	}
	
	
	  @RequestMapping(method = RequestMethod.GET, value="/downloadFile/{appFolder}")
	public @ResponseBody  byte[] downloadGenerico(     HttpServletResponse response, @PathVariable("appFolder") String appFolder,@RequestParam(name = "file") String file) {
		logger.debug("urlBase:::" + urlBase);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment",  "attachment; filename=\"" + file + "\""); 
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		headers.add("Content-Disposition","attachment;filename="+ file );  
		HttpEntity<String> entity = new HttpEntity<String>(headers);
        String msg="ok";
        byte[] downoladFile=null;
        try {
         downoladFile=restTemplate
				.exchange(urlBase + "/downloadFile/" + appFolder + "?file=" + file, HttpMethod.GET, entity, byte[].class)
				.getBody();
         response.reset();
	    response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename="+file);
        } catch (Exception ex) {
	    	logger.debug(ex.getMessage(),ex);
	    	msg="nook";
	    	downoladFile="nook".getBytes();
        }
        return downoladFile;
	}
	
	
}
