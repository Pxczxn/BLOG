package com.pxczxn.blog.community.post.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostTagId implements Serializable {
    private Long postId;
    private Long tagId;
}
