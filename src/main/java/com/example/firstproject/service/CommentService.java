package com.example.firstproject.service;

import com.example.firstproject.dto.CommentDto;
import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;


    public List<CommentDto> comments(Long articleId) {
//        // 1. 댓글 조회
//        List<Comment> comments = commentRepository.findByArticleId(articleId);
//
//        // 2. 엔티티 -> DTO 변환
//        List<CommentDto> dtos = new ArrayList<>();
//        for(int i = 0 ; i < comments.size() ; i ++){
//            Comment c = comments.get(i);
//            CommentDto dto = CommentDto.createCommentDto(c);
//            dtos.add(dto);
//        }
//
//        // 3. 결과 반환
        return commentRepository.findByArticleId(articleId)
                .stream()
                .map(comment -> CommentDto.createCommentDto(comment))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto create(Long articleId, CommentDto dto) {
        // 1. DB에서 부모 게시글을 조회해 가져오고 없을 경우 예외 발생시키기
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! " + "대상 게시글이 없습니다."));

        // 2. 부모 게시글의 새 댓글 엔티티 생성하기
        Comment comment = Comment.createComment(dto, article);

        // 3. 생성된 엔티티를 DB에 저장하기
        Comment created = commentRepository.save(comment);

        // 4. DB에 저장한 엔티티를 DTO로 변환해 반환하기
        return CommentDto.createCommentDto(created);

    }

    @Transactional
    public CommentDto update(Long id, CommentDto dto) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id).
                orElseThrow(()-> new IllegalArgumentException("댓글 수정 실패!  " + "대상 댓글이 없습니다"));

        // 2. 댓글 수정
        target.patch(dto);

        // 3. DB로 갱신
        Comment updated = commentRepository.save(target);

        // 4. 댓글 엔티티를 Dto로 변환 및 반환
        return CommentDto.createCommentDto(updated);

    }

    @Transactional
    public CommentDto delete(Long id) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("댓글 삭제 실패!" + "대상이 업습니다"));


        // 2. 댓글 삭제
        commentRepository.delete(target);

        // 3. 삭제 댓글을 DTO로 변환 및 반환
        return CommentDto.createCommentDto(target);

    }

    @Transactional
    public void deleteArticle(Long articleId) {
        // 1. 댓글 목록 조회 및 예외 처리
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        if(comments.size() == 0){
            return;
        }
        // 2. 댓글 목록 삭제
        comments.forEach(comment -> delete(comment.getId()));

    }
}
