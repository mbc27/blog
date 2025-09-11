package com.blog.controller;

import com.blog.entity.*;
import com.blog.service.*;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台关于我管理控制器
 */
@RestController
@RequestMapping("/api/admin/about")
@CrossOrigin
public class AdminAboutController {

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
     * 获取基本信息
     */
    @GetMapping("/basic")
    public Result getBasicInfo() {
        AboutMe aboutMe = aboutMeService.getAboutMe();
        return Result.success(aboutMe);
    }

    /**
     * 更新基本信息
     */
    @PutMapping("/basic")
    public Result updateBasicInfo(@RequestBody AboutMe aboutMe) {
        boolean success = aboutMeService.updateAboutMe(aboutMe);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 获取工作经历列表
     */
    @GetMapping("/work-experience")
    public Result getWorkExperience() {
        List<WorkExperience> list = workExperienceService.getAllWorkExperience();
        return Result.success(list);
    }

    /**
     * 添加工作经历
     */
    @PostMapping("/work-experience")
    public Result addWorkExperience(@RequestBody WorkExperience workExperience) {
        boolean success = workExperienceService.addWorkExperience(workExperience);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新工作经历
     */
    @PutMapping("/work-experience/{id}")
    public Result updateWorkExperience(@PathVariable Long id, @RequestBody WorkExperience workExperience) {
        workExperience.setId(id);
        boolean success = workExperienceService.updateWorkExperience(workExperience);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除工作经历
     */
    @DeleteMapping("/work-experience/{id}")
    public Result deleteWorkExperience(@PathVariable Long id) {
        boolean success = workExperienceService.deleteWorkExperience(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取教育背景列表
     */
    @GetMapping("/education")
    public Result getEducation() {
        List<Education> list = educationService.getAllEducation();
        return Result.success(list);
    }

    /**
     * 添加教育背景
     */
    @PostMapping("/education")
    public Result addEducation(@RequestBody Education education) {
        boolean success = educationService.addEducation(education);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新教育背景
     */
    @PutMapping("/education/{id}")
    public Result updateEducation(@PathVariable Long id, @RequestBody Education education) {
        education.setId(id);
        boolean success = educationService.updateEducation(education);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除教育背景
     */
    @DeleteMapping("/education/{id}")
    public Result deleteEducation(@PathVariable Long id) {
        boolean success = educationService.deleteEducation(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取技能列表
     */
    @GetMapping("/skills")
    public Result getSkills() {
        List<Skill> list = skillService.getAllSkillsWithCategory();
        return Result.success(list);
    }

    /**
     * 添加技能
     */
    @PostMapping("/skills")
    public Result addSkill(@RequestBody Skill skill) {
        boolean success = skillService.addSkill(skill);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新技能
     */
    @PutMapping("/skills/{id}")
    public Result updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        boolean success = skillService.updateSkill(skill);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除技能
     */
    @DeleteMapping("/skills/{id}")
    public Result deleteSkill(@PathVariable Long id) {
        boolean success = skillService.deleteSkill(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取兴趣爱好列表
     */
    @GetMapping("/interests")
    public Result getInterests() {
        List<Interest> list = interestService.getAllInterests();
        return Result.success(list);
    }

    /**
     * 添加兴趣爱好
     */
    @PostMapping("/interests")
    public Result addInterest(@RequestBody Interest interest) {
        boolean success = interestService.addInterest(interest);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新兴趣爱好
     */
    @PutMapping("/interests/{id}")
    public Result updateInterest(@PathVariable Long id, @RequestBody Interest interest) {
        interest.setId(id);
        boolean success = interestService.updateInterest(interest);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除兴趣爱好
     */
    @DeleteMapping("/interests/{id}")
    public Result deleteInterest(@PathVariable Long id) {
        boolean success = interestService.deleteInterest(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取项目列表
     */
    @GetMapping("/projects")
    public Result getProjects() {
        List<Project> list = projectService.getAllProjects();
        return Result.success(list);
    }

    /**
     * 添加项目
     */
    @PostMapping("/projects")
    public Result addProject(@RequestBody Project project) {
        boolean success = projectService.addProject(project);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新项目
     */
    @PutMapping("/projects/{id}")
    public Result updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        boolean success = projectService.updateProject(project);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/projects/{id}")
    public Result deleteProject(@PathVariable Long id) {
        boolean success = projectService.deleteProject(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}