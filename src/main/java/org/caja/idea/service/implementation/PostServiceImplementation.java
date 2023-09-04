package org.caja.idea.service.implementation;

import org.caja.idea.entity.dto.postDto.PostDTO;
import org.caja.idea.entity.dto.postDto.PostListDto;
import org.caja.idea.entity.mapper.PostMapper;
import org.caja.idea.entity.models.Post;
import org.caja.idea.entity.models.Users;
import org.caja.idea.exception.PostNotFoundException;
import org.caja.idea.exception.TitleNotFoundException;
import org.caja.idea.exception.UnauthorizedException;
import org.caja.idea.exception.UserNotFoundException;
import org.caja.idea.repository.IPostRepository;
import org.caja.idea.repository.IUserRepository;
import org.caja.idea.service.interfaces.IPostService;
import org.caja.idea.utils.constants.Message;
import org.caja.idea.utils.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImplementation  implements IPostService {

    @Autowired
    private IPostRepository repository;
    @Autowired
    private IUserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> findAllPost() {
        return repository.findAll()
                .stream()
                .map((dto) -> new PostDTO(dto.getUsers().getUsername(), dto.getId(), dto.getTitle(), dto.getContent(),
                        dto.getCreated(), dto.getUpdated()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO findPostById(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(Message.POST_NOT_FOUND, 404, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        return PostMapper.toPostDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO findPostByTitle(String title) {
        Post post = repository.findByTitle(title)
                .orElseThrow(() -> new TitleNotFoundException(Message.TITLE_NOT_FOUND, 404, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        return PostMapper.toPostDto(post);
    }


    @Override
    @Transactional
    public ApiResponse createPost(PostDTO postDTO) {
        Users users = userRepository.findByUsername(postDTO.username())
                .orElseThrow(() -> new UserNotFoundException(Message.USER_NOT_FOUND, 404, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        Post post = PostMapper.toPostDto(postDTO, users);
        repository.save(post);
        return new ApiResponse(Message.POST_SAVE_SUCCESSFULLY,201, HttpStatus.CREATED, LocalDateTime.now());
    }



    @Override
    @Transactional
    public PostDTO update(PostDTO postDTO, Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(Message.POST_NOT_FOUND, 404, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        // Verificar si el usuario actual coincide con el autor del post
        if(!post.getUsers().getUsername().equals(postDTO.username())){
            throw  new UnauthorizedException(Message.USER_NOT_AUTHORIZED, Message.WITHOUT_PERMISSION, HttpStatus.UNAUTHORIZED);
        }
        // Actualizar los campos del post con los valores proporcionados en postDTO
        post.setTitle(postDTO.title());
        post.setContent(postDTO.content());
        repository.save(post);
       return PostMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public ApiResponse delete(Long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(Message.POST_NOT_FOUND, 404, HttpStatus.NOT_FOUND, LocalDateTime.now()));
        repository.delete(post);
        return new ApiResponse(Message.POST_DELETE_SUCCESSFULLY, 200, HttpStatus.OK, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public PostListDto findPostByUserId(Long userId) {
        return null;
    }

}