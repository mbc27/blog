package com.blog.controller;

import com.blog.entity.*;
import com.blog.service.*;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关于我页面控制器
 */
@RestController
@RequestMapping("/api/about")
@CrossOrigin
public class AboutMeController {

    @Autowired
    private AboutMeService aboutMeService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private InterestService interestService;

    @Autowired
    private ProjectService projectService;

    /**
     * 获取关于我的完整信息
     */
    @GetMapping("/info")
    public Result getAboutInfo() {
        Map<String, Object> data = new HashMap<>();
        
        // 基本信息
        AboutMe aboutMe = aboutMeService.getAboutMe();
        data.put("basicInfo", aboutMe);
        
        // 工作经历
        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperience();
        data.put("workExperiences", workExperiences);
        
        // 教育背景
        List<Education> educations = educationService.getAllEducation();
        data.put("educations", educations);
        
        // 技能
        List<Skill> skills = skillService.getAllSkillsWithCategory();
        data.put("skills", skills);
        
        // 兴趣爱好
        List<Interest> interests = interestService.getAllInterests();
        data.put("interests", interests);
        
        // 精选项目
        List<Project> featuredProjects = projectService.getFeaturedProjects();
        data.put("featuredProjects", featuredProjects);
        
        return Result.success(data);
    }

    /**
     * 获取基本信息
     */
    @GetMapping("/basic")
    public Result getBasicInfo() {
        AboutMe aboutMe = aboutMeService.getAboutMe();
        return Result.success(aboutMe);
    }

    /**
     * 获取工作经历
     */
    @GetMapping("/work-experience")
    public Result getWorkExperience() {
        List<WorkExperience> workExperiences = workExperienceService.getAllWorkExperience();
        return Result.success(workExperiences);
    }

    /**
     * 获取教育背景
     */
    @GetMapping("/education")
    public Result getEducation() {
        List<Education> educations = educationService.getAllEducation();
        return Result.success(educations);
    }

    /**
     * 获取技能
     */
    @GetMapping("/skills")
    public Result getSkills() {
        List<Skill> skills = skillService.getAllSkillsWithCategory();
        return Result.success(skills);
    }

    /**
     * 获取兴趣爱好
     */
    @GetMapping("/interests")
    public Result getInterests() {
        List<Interest> interests = interestService.getAllInterests();
        return Result.success(interests);
    }

    /**
     * 获取所有项目
     */
    @GetMapping("/projects")
    public Result getProjects() {
        List<Project> projects = projectService.getAllPublicProjects();
        return Result.success(projects);
    }

    /**
     * 获取精选项目
     */
    @GetMapping("/projects/featured")
    public Result getFeaturedProjects() {
        List<Project> projects = projectService.getFeaturedProjects();
        return Result.success(projects);
    }

    /**
     * 根据分类获取项目
     */
    @GetMapping("/projects/category/{categoryId}")
    public Result getProjectsByCategory(@PathVariable Long categoryId) {
        List<Project> projects = projectService.getProjectsByCategory(categoryId);
        return Result.success(projects);
    }

    /**
     * 项目详情（增加浏览量）
     */
    @GetMapping("/projects/{id}")
    public Result getProjectDetail(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project != null && Boolean.TRUE.equals(project.getIsPublic())) {
            // 增加浏览量
            projectService.incrementViewCount(id);
            return Result.success(project);
        }
        return Result.error("项目不存在或不公开");
    }

    /**
     * 项目点赞
     */
    @PostMapping("/projects/{id}/like")
    public Result likeProject(@PathVariable Long id, 
                             @RequestParam(required = false) Long userId, 
                             @RequestParam(required = false) String ipAddress,
                             javax.servlet.http.HttpServletRequest request) {
        
        // 如果没有传递IP地址，从请求中获取
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            ipAddress = getClientIpAddress(request);
        }
        
        // 如果没有传递用户ID，尝试从当前认证用户获取
        if (userId == null) {
            try {
                org.springframework.security.core.Authentication authentication = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getPrincipal().equals("anonymousUser")) {
                    userId = Long.parseLong(authentication.getName());
                }
            } catch (Exception e) {
                // 忽略异常，使用IP地址进行点赞
            }
        }
        
        Map<String, Object> result = projectService.toggleLike(id, userId, ipAddress);
        return Result.success(result);
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(javax.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 增加项目浏览量
     */
    @PostMapping("/projects/{id}/view")
    public Result incrementProjectView(@PathVariable Long id) {
        boolean success = projectService.incrementViewCount(id);
        if (success) {
            return Result.success("浏览量增加成功");
        }
        return Result.error("增加浏览量失败");
    }
}