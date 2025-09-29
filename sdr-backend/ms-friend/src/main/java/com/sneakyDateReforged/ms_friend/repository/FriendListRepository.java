package com.sneakyDateReforged.ms_friend.repository;

import com.sneakyDateReforged.ms_friend.entity.FriendList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendListRepository extends JpaRepository<FriendList, Long> {
    List<FriendList> findByUserId(Long userId);
}
