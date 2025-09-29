package com.sneakyDateReforged.ms_friend.repository;

import com.sneakyDateReforged.ms_friend.entity.FriendListMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendListMemberRepository extends JpaRepository<FriendListMember, Long> {
    List<FriendListMember> findByFriendListId(Long friendListId);
}
