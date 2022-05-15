package com.unicen.app.services;

import com.unicen.app.exceptions.ObjectNotFoundException;
import com.unicen.app.model.User;
import com.unicen.app.model.ValidationCode;
import com.unicen.app.model.ValidationType;
import com.unicen.app.repositories.ValidationCodeRepository;

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
