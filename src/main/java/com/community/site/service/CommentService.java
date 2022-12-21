package com.community.site.service;

import com.community.site.dto.CommentDto.*;
import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.CommentRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.entity.BoardList;
import com.community.site.entity.Comment;
import com.community.site.entity.User;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.error.ErrorCode.ACCESS_DENIED_EXCEPTION;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Transactional  // 부모 댓글을 작성하는 기능이다.
    public String createParentComment(Long id, CommentRequestDto commentDto, HttpServletRequest request,
                                      HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        Comment comment = Comment.parent(user, boardList, commentDto.getComment(), commentDto);
        commentRepository.save(comment);

        return "부모 댓글 저장 완료";
    }

    @Transactional  // 대댓글을 작성하기 위한 기능이다.
    public String createChildComment(CommentChildRequestDto commentDto, HttpServletRequest request,
                                     HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .comment(commentDto.getComment())
                .createdDate(commentDto.getCreatedDate())
                .modifiedDate(commentDto.getModifiedDate())
                .build();

        Comment child = validateComment(commentDto.getParentId(), user.getId(), commentRequestDto);
        commentRepository.save(child);

        return "자식 댓글 저장 완료";
    }

    // 부모 댓글인지 대댓글인지 판별하는 기능이다.
    private Comment validateComment(Long parentId, Long userId, CommentRequestDto commentRequestDto) {
        Comment parent = commentRepository.getById(parentId);
        if (!parent.isParent()) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        User user = userRepository.findById(userId).orElseThrow();

        return Comment.child(user, parent.getBoardList(), commentRequestDto.getComment(),
                commentRequestDto, parent);
    }

    @Transactional  // 댓글을 불러오는 기능이다.
    public List<CommentResponseDto> readComment(Long id) {
        List<Comment> comments = commentRepository.findByBoardListIdAndParentIsNull(id);

        return comments.stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }

    @Transactional  // 댓글을 수정할 수 있는 기능이다.
    public void updateComment(CommentUpdateRequestDto commentRequestDto, HttpServletRequest request,
                              HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        Comment comment = commentRepository.getById(commentRequestDto.getId());

        if (!comment.getUser().getNickname().equals(user.getNickname())) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        comment.update(commentRequestDto);
    }

    @Transactional  // 댓글을 삭제할 수 있는 기능이다.
    public void deleteComment(CommentDeleteRequestDto requestDto, HttpServletRequest request,
                              HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        Comment comment = commentRepository.getById(requestDto.getId());

        if (!comment.getUser().getNickname().equals(user.getNickname())) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        commentRepository.delete(comment);
    }
}
