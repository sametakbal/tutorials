package com.sametakbal.api.service.impl;

import com.sametakbal.api.common.GeneralException;
import com.sametakbal.api.entity.User;
import com.sametakbal.api.repository.IUserRepository;
import com.sametakbal.api.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            if (user.getIdentityNo() == null || user.getIdentityNo().length() != 11) {
                throw new GeneralException("Invalid Identity No");
            }
            if (userRepository.existsByIdentityNo(user.getIdentityNo())) {
                throw new GeneralException("Identity No Already Exists");
            }
        }
        return userRepository.save(user);
    }

    @Override
    public User getById(Integer id) throws GeneralException{
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new GeneralException("User not found!");
        }
        return userOptional.get();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new GeneralException("User Not Found");
        }
        userRepository.deleteById(id);
    }


}
