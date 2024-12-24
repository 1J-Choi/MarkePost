package com.markepost.suspend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.markepost.suspend.constant.SuspendType;
import com.markepost.suspend.entity.SuspendEntity;

public interface SuspendRepository extends JpaRepository<SuspendEntity, Integer>{
	public Optional<SuspendEntity> findFirstByUserIdAndBoardIdAndSuspendTypeOrderByCreatedAtDesc(
			int userId, int boardId, SuspendType suspendType);
}
