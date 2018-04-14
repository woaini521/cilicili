package cilicili.jz2.controller.impl;

import cilicili.jz2.controller.IVideoController;
import cilicili.jz2.controller.baseController;
import cilicili.jz2.pojo.Video;
import cilicili.jz2.service.impl.VideoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping ("/video")
public class VideoControllerImpl extends baseController implements IVideoController {
	private final VideoServiceImpl videoService;
	
	@Autowired
	public VideoControllerImpl(VideoServiceImpl videoService) {
		this.videoService = videoService;
	}
	
	@RequestMapping ("/findId")
	@ResponseBody
	@Override
	public Map<String, Serializable> findVideoById(int id) {
		Video video = videoService.findVideoById(id);
		if (video == null) {
			result.put("status", "failure");
			result.put("msg", "找不到该视频");
		} else {
			result.put("status", "success");
			result.put("video", video);
		}
		return result;
	}
	
	@RequestMapping ("/add")
	@ResponseBody
	@Override
	public Map<String, Serializable> addVideo(Video video) {
		result.put("status", "failure");
		do {
			if (video.getTitle() == null) {
				result.put("msg", "视频标题为空");
				break;
			} else if (video.getTitle().length() == 0 || video.getTitle().length() >= 50) {
				result.put("msg", "视频标题为空或超过50长度限制");
				break;
			}
			if (video.getUrl() == null) {
				result.put("msg", "视频地址为空");
				break;
			} else if (video.getUrl().length() == 0 || video.getUrl().length() >= 100) {
				result.put("msg", "视频地址为空或超过100长度限制");
				break;
			}
			if (video.getUploadUserid() == null) {
				result.put("msg", "上传用户id为空");
				break;
			}
			if (video.getPicUrl() == null) {
				result.put("msg", "视频封面地址为空");
				break;
			} else if (video.getPicUrl().length() == 0 || video.getPicUrl().length() >= 100) {
				result.put("msg", "视频封面地址为空或超过100长度限制");
				break;
			}
			video.setId(null);
			video.setUploadTime(ZonedDateTime.now());
			video.setCountPlay(0);
			video.setCountLike(0);
			try {
				videoService.addVideo(video);
				result.put("status", "success");
				video = videoService.findVideoByUrl(video.getUrl());
				result.put("video", video);
			} catch (Exception e) {
			}
		} while (false);
		return result;
	}
	
	@RequestMapping ("/play")
	@ResponseBody
	@Override
	public Map<String, Serializable> playVideo(Integer id) {
		Video video = videoService.findVideoById(id);
		try {
			video.setCountPlay(video.getCountPlay() + 1);
			videoService.updateVideo(video);
			result.put("status", "success");
			result.put("count_play", video.getCountPlay());
		} catch (Exception e) {
			result.put("status", "failure");
			result.put("msg", "参数错误");
		}
		return result;
	}
	
	@RequestMapping ("/like")
	@ResponseBody
	@Override
	public Map<String, Serializable> likeVideo(Integer id) {
		Video video = videoService.findVideoById(id);
		try {
			video.setCountLike(video.getCountLike() + 1);
			videoService.updateVideo(video);
			result.put("status", "success");
			result.put("count_like", video.getCountLike());
		} catch (Exception e) {
			result.put("status", "failure");
			result.put("msg", "参数错误");
		}
		return result;
	}
	
	@RequestMapping ("/show")
	@ResponseBody
	@Override
	public Map<String, Serializable> showVideos() {
		result.put("videos", (ArrayList<Video>)videoService.showVideos());
		return result;
	}
	
	@Override
	public Map<String, Serializable> updateUser(Video video) {
		return null;
	}
	
	@ResponseBody
	@ExceptionHandler ({Exception.class})
	public Map<String, Serializable> exceptionHandle(Exception e) {
		result.clear();
		result.put("status", "failure");
		result.put("msg", "未登录或参数错误");
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.error(e.getMessage());
		logger.error(e.getLocalizedMessage());
		return result;
	}
}