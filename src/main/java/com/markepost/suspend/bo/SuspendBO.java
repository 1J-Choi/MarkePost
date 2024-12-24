package com.markepost.suspend.bo;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.markepost.suspend.constant.SuspendType;
import com.markepost.suspend.entity.SuspendEntity;
import com.markepost.suspend.repository.SuspendRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SuspendBO {
	private final SuspendRepository suspendRepository;
	
	public SuspendEntity addSuspend(
			int userId, int boardId, SuspendType suspendType, 
			int number, String time, int adminId) {
		// 제제 기간
		LocalDateTime untillTime;
		
		// time에 따라 분기하며 그 기간은 number 만큼 추가
		switch (time) {
	    case "H": // 시간 단위
	        untillTime = LocalDateTime.now().plusHours(number);
	        break;
	    case "D": // 일 단위
	        untillTime = LocalDateTime.now().plusDays(number);
	        break;
	    case "W": // 주 단위
	        untillTime = LocalDateTime.now().plusWeeks(number);
	        break;
	    case "Y": // 년 단위
	        untillTime = LocalDateTime.now().plusYears(number);
	        break;
	        // 잘못된 time값 들어올 시 throw함
	    default:
	        throw new IllegalArgumentException("Invalid time unit");
		}
		
		SuspendEntity suspend = SuspendEntity.builder()
				.userId(userId)
				.boardId(boardId)
				.adminId(adminId)
				.suspendType(suspendType)
				.untillTime(untillTime)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
		
		return suspendRepository.save(suspend);
	}
	
	public SuspendEntity getSuspend(int userId, int boardId, SuspendType suspendType){
		return suspendRepository.findFirstByUserIdAndBoardIdAndSuspendTypeOrderByCreatedAtDesc(
				userId, boardId, suspendType).orElse(null);
	}
}
