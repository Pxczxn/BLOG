


package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostInteractionResponse {

    
    private Long postId;
    
    private long likeCount;
    
    private long favoriteCount;
    
    private boolean likedByMe;
    
    private boolean favoritedByMe;
}

