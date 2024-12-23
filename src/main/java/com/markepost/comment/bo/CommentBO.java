package com.markepost.comment.bo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.markepost.admin.bo.AdminBO;
import com.markepost.admin.entity.AdminEntity;
import com.markepost.board.bo.BoardBO;
import com.markepost.board.domain.Board;
import com.markepost.comment.domain.Comment;
import com.markepost.comment.domain.CommentDTO;
import com.markepost.comment.domain.CommentTopDTO;
import com.markepost.comment.domain.SubComment;
import com.markepost.comment.domain.SubCommentDTO;
import com.markepost.comment.mapper.CommentMapper;
import com.markepost.post.bo.PostBO;
import com.markepost.post.domain.Post;
import com.markepost.user.bo.UserBO;
import com.markepost.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentBO{
	private final CommentMapper commentMapper;
	private final UserBO userBO;
	private final AdminBO adminBO;
	private final BoardBO boardBO;
	
	public int addComment(int postId, String content, 
			Integer upperCommentId, int userId) {
		if (upperCommentId == null) { // 메인 댓글인 경우
			return commentMapper.insertComment(postId, content, userId);
		} else { // 서브 댓글인 경우
			return commentMapper.insertSubComment(postId, content, upperCommentId, userId);
		}
	}
	
	public List<CommentDTO> getCommentDTOList(Post post) {
		List<CommentDTO> commentList = new ArrayList<>();
		List<Comment> comments = commentMapper.getCommentListByPostId(post.getId());
		
		for(Comment comment : comments) {
			CommentDTO commentDTO = new CommentDTO();
			UserEntity user = userBO.getUserEntityById(comment.getUserId());
			commentDTO.setComment(comment);
			commentDTO.setUserName(user.getName());
			commentDTO.setComfirmed(user.isConfirmed());
			Board board = boardBO.getBoardById(post.getBoardId());
			AdminEntity userAdmin = adminBO.getAdminEntityById(board.getId(), user.getId());
			if(userAdmin == null) {
				commentDTO.setAdmin(false);
				commentDTO.setMain(false);
			} else {
				commentDTO.setAdmin(true);
				commentDTO.setMain(userAdmin.isAdminType());
			}
			List<SubCommentDTO> subCommentList = new ArrayList<>();
			List<SubComment> subComments = commentMapper.getSubCommentListByCommentId(comment.getId());
			for(SubComment subComment : subComments) {
				SubCommentDTO subCommentDTO = new SubCommentDTO();
				UserEntity subUser = userBO.getUserEntityById(subComment.getUserId());
				subCommentDTO.setSubComment(subComment);
				subCommentDTO.setUserName(subUser.getName());
				subCommentDTO.setComfirmed(subUser.isConfirmed());
				AdminEntity subUserAdmin = adminBO.getAdminEntityById(board.getId(), subUser.getId());
				if(subUserAdmin == null) {
					subCommentDTO.setAdmin(false);
					subCommentDTO.setMain(false);
				} else {
					subCommentDTO.setAdmin(true);
					subCommentDTO.setMain(subUserAdmin.isAdminType());
				}
				subCommentList.add(subCommentDTO);
			}
			commentDTO.setSubCommentList(subCommentList);
			commentList.add(commentDTO);
		}
		
		return commentList;
	}
	
	public int updateCommentDeleted(String commentType, int id) {
		if(commentType.equals("COMMENT")) { // 댓글 isDeleted 전환
			return commentMapper.updateCommentDeleted(id);
		} else if(commentType.equals("SUBCOMMENT")) {
			return commentMapper.updateSubCommentDeleted(id);
		}
		return 0;
	}
	
	public void deleteCommentsByPostId(int postId) {
		commentMapper.deleteCommentByPostId(postId);
		commentMapper.deleteSubCommentByPostId(postId);
	}
	
	public Comment getCommentByFkId(int fkId) {
		return commentMapper.selectCommentById(fkId);
	}
	
	public SubComment getSubCommentByFkId(int fkId) {
		return commentMapper.selectSubCommentById(fkId);
	}
	
	public List<CommentTopDTO> getTop5CommentList(Integer userId) {
		List<CommentTopDTO> commentTopDTOs = new ArrayList<>();
		// Comment와 SubComment중에서 userId가 같고 가장 최근인 5개 가져오기
		List<Comment> comments = commentMapper.selectCommentTopListByUserId(userId);
		for(Comment comment: comments) {
			CommentTopDTO commentTopDTO = new CommentTopDTO();
			commentTopDTO.setContent(comment.getContent());
			commentTopDTO.setPostId(comment.getPostId());
			commentTopDTO.setCreatedAt(comment.getCreatedAt());
			
			commentTopDTOs.add(commentTopDTO);
		}
		
		return commentTopDTOs;
	}
	
	public void updateCommentDeletedByPostId(int postId) {
		List<Comment> comments = commentMapper.getCommentListByPostId(postId);
		List<SubComment> subComments = commentMapper.getSubCommentListByCommentId(postId);
		for(Comment comment : comments) {
			commentMapper.updateCommentDeleted(comment.getId());
		}
		for(SubComment subComment : subComments) {
			commentMapper.updateSubCommentDeleted(subComment.getId());
		}
	}
}
