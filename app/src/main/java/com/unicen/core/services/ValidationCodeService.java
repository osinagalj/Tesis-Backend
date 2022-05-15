package com.unicen.core.services;

import com.unicen.core.exceptions.ObjectNotFoundException;
import com.unicen.core.model.User;
import com.unicen.core.model.ValidationCode;
import com.unicen.core.model.ValidationType;
import com.unicen.core.repositories.ValidationCodeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Service
public class ValidationCodeService extends CrudService<ValidationCode, ValidationCodeRepository> {

    public ValidationCodeService(ValidationCodeRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(ValidationCode existingObject, ValidationCode updatedObject) {
        existingObject.setValidationInformation(updatedObject.getValidationInformation());
        existingObject.setCode(updatedObject.getCode());
        existingObject.setType(updatedObject.getType());
    }

    @Override
    protected Class<ValidationCode> getObjectClass() {
        return ValidationCode.class;
    }

    @Transactional
    // TODO check if it makes sense to throw an Exception here or not
    public void useCode(String code, User user, Runnable action) {
        ValidationCode validationCode = getValidationCode(code, user.getEmail());

        action.run();
        repository.save(validationCode.expire());
    }

    private ValidationCode getValidationCode(String code, String valInfo) {
        return repository.findByCodeAndValidationInformation(code, valInfo).orElseThrow(() -> new ObjectNotFoundException(code.getClass(), "code", code));
    }

    @Transactional(readOnly = true)
    public ValidationCode findByValidationInformation(String email, Supplier<ValidationCode> supplier) {
        return repository.findByValidationInformation(email).orElseGet(supplier);
    }

    @Transactional
    public ValidationCode createForEmailValidation(User user) {
        ValidationCode validationCode = new ValidationCode(ValidationType.EMAIL, user.getEmail(),
                Integer.toString(ThreadLocalRandom.current().nextInt(100000, 999999)), user);
        return repository.save(validationCode);
    }

    @Transactional
    public ValidationCode createForLoginViaEmail(User user) {
        ValidationCode validationCode = new ValidationCode(ValidationType.EMAIL, user.getEmail(),
                Integer.toString(ThreadLocalRandom.current().nextInt(100000, 999999)), user);
        return repository.save(validationCode);

    }
}
