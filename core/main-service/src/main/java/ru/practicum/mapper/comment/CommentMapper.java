package ru.practicum.mapper.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.mapper.user.UserMapper;
import ru.practicum.model.Comment;

/**
 * The interface Comment mapper.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {

    /**
     * To dto comment dto.
     *
     * @param comment the comment
     * @return the comment dto
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "created", source = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updated", source = "updated", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "replyComment", source = "parent", qualifiedByName = "toDtoRecursive")
    CommentDto toDto(Comment comment);

    /**
     * To dto recursive comment dto.
     *
     * @param comment the comment
     * @return the comment dto
     */
    @Named("toDtoRecursive")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "created", source = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updated", source = "updated", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "replyComment", ignore = true) // Предотвращение бесконечной рекурсии
    CommentDto toDtoRecursive(Comment comment);
    }
