package com.vityazev_egor.debt_clear_flow_server.Controllers.WebInterface;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.vityazev_egor.debt_clear_flow_server.Misc.Shared;
import com.vityazev_egor.debt_clear_flow_server.Models.Teacher;
import com.vityazev_egor.debt_clear_flow_server.Models.TeacherRepo;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/panel/account/")
public class AccountController {

    @Autowired
    private TeacherRepo teacherRepo;

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private Teacher getTeacherFromSession(HttpSession session){
        Integer id = (Integer) session.getAttribute("id");
        return teacherRepo.findById(id).orElse(null);
    }

    @GetMapping("/")
    public ModelAndView getAccountView(HttpSession session){
        Teacher currentUser = getTeacherFromSession(session);
        
        logger.info("Got teacher with id = "+ currentUser.id);
        return new ModelAndView("account", "currentUser", currentUser);
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(HttpSession session, @RequestParam String currentPassword, @RequestParam String newPassword){
        Teacher currentUser = getTeacherFromSession(session);
        ModelAndView modelAndView = new ModelAndView("account", "currentUser", currentUser);

        logger.info("Got rs in 'changePassword' - " + currentPassword + ":" + newPassword);
        if(currentUser.password.equals(currentPassword)){
            currentUser.password = newPassword;
            teacherRepo.save(currentUser);
            logger.info("Password changed for user - " + currentUser.login);
            modelAndView.addObject("message", "Пароль был успешно изменён!");
            return modelAndView;
        }
        else{
            logger.warn("Current password is not correct for user - "+ currentUser.login);
            modelAndView.addObject("errorMessage", "Введеённый пароль не совпадает с текущим");
            return modelAndView;
        }
    }

    @SuppressWarnings("null")
    @PostMapping("/setImage")
    public ModelAndView setImage(HttpSession session, @RequestParam MultipartFile newProfileImage){
        Teacher currentUser = getTeacherFromSession(session);
        logger.info("Got file - " + newProfileImage.getOriginalFilename());
        var view = new ModelAndView("account", "currentUser", currentUser);

        // проверяем размер файла
        if (newProfileImage.getSize() > 5242880){
            logger.warn("File is too large");
            view.addObject("errorMessage", "Файл слишком большой");
            return view;
        }

        // проверяем тип файла
        if (newProfileImage.getContentType() == null){
            logger.warn("There is no file type");
            view.addObject("errorMessage", "Неизвестный тип файла");
            return view;
        }

        logger.info("FileContent type = " + newProfileImage.getContentType());
        // check that file is image
        if (!newProfileImage.getContentType().startsWith("image")){
            logger.warn("File is not an image");
            view.addObject("errorMessage", "Файл не является изображением");
            return view;
        }

        // get file extension from file name
        String fileExtension = newProfileImage.getOriginalFilename().substring(newProfileImage.getOriginalFilename().lastIndexOf("."));
        logger.info("File extension - " + fileExtension);

        String newFileName = Shared.generateRandomString(10) + fileExtension;
        logger.info("New file name - " + newFileName);

        // delete original file if is not present
        if (currentUser.imageName != null && !currentUser.imageName.contains("defaultImage")){
            logger.info("Deleting old file - " + currentUser.imageName);
            try{
                Paths.get(Shared.imagesDirectory.toString(), currentUser.imageName).toFile().delete();
            } catch (Exception ex){
                logger.error("Can't delete old image", ex);
            }
        }   
        
        try{
            newProfileImage.transferTo(Paths.get(Shared.imagesDirectory.toString(), newFileName).toFile());
        }
        catch(Exception e){
            logger.error("Can't save new image file", e);
            view.addObject("errorMessage", "Ошибка при сохранении файла");
            return view;
        }

        currentUser.imageName = newFileName;
        teacherRepo.save(currentUser);
        view.addObject("message", "Аватар профиля был успешно обновлён!");
        return view;
    }
}
