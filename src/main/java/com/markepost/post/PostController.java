package com.markepost.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.markepost.board.bo.BoardBO;
import com.markepost.post.bo.PostBO;
import com.markepost.post.domain.PostDetailDTO;
import com.markepost.post.domain.PostUpdateDTO;
import com.markepost.suspend.bo.SuspendBO;
import com.markepost.suspend.constant.SuspendType;
import com.markepost.suspend.entity.SuspendEntity;
import com.markepost.tag.bo.TagBO;
import com.markepost.tag.domain.PostTagDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
	private final TagBO tagBO;
	private final BoardBO boardBO;
	private final PostBO postBO;
	private final SuspendBO suspendBO;
	
	@GetMapping("/create-post-view")
	public String createPost(
			@RequestParam("boardId") int boardId,
			Model model, HttpSession session) {
		//TODO: suspend에 따른 게시글 작성 차단
		String boardName = boardBO.getBoardById(boardId).getName();
		List<PostTagDTO> postTagDTOList = tagBO.getpostTagDTOListByBoardId(boardId);
		model.addAttribute("postTagDTOList", postTagDTOList);
		model.addAttribute("boardId", boardId);
		model.addAttribute("boardName", boardName);
		return "post/createPost";
	}
	
	@GetMapping("/post-detail-view")
	public String postDetail(
			@RequestParam("postId") int postId, 
			Model model, HttpSession session, 
			RedirectAttributes redirectAttributes) {
		Integer userId = (Integer) session.getAttribute("userId");
		PostDetailDTO postDetail = postBO.getPostDetailDTOById(postId, userId);
		
		if(userId != null) {
			SuspendEntity suspend = suspendBO.getSuspend(userId, postDetail.getPost().getBoardId(), SuspendType.BOARD);
			LocalDateTime now = LocalDateTime.now();
			if(suspend != null && suspend.getUntillTime().compareTo(now) > 0 ) {
				// Map을 반환하는 responseBody가 아님으로
				// redirectAttributes에 담아 보낸 뒤 javaScript에서 처리하게 한다.
				// model에 담으면 redirect로 연결된 메소드에 보낼 수 없기 때문
				redirectAttributes.addFlashAttribute("suspendMessage", 
						"현재 게시판 접속 정지 상태입니다."
						+ "\n정지기간: " + suspend.getUntillTime());
				return "redirect:/suspend/suspendView";
			}
		}
		
		
		
		model.addAttribute("postDetail", postDetail);
		return "post/postDetail";
	}
	
	@GetMapping("/update")
	public String updatePost(
			@RequestParam("postId") int postId, Model model) {
		PostUpdateDTO postUpdateDTO = postBO.getPostUpdateDTO(postId);
		
		model.addAttribute("postUpdateDTO", postUpdateDTO);
		
		return "post/updatePost";
	}
}
