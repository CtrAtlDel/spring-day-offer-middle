package com.onedayoffer.taskdistribution.controllers;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.controllers.exceptions.EmployeeNotFoundException;
import com.onedayoffer.taskdistribution.controllers.exceptions.TaskNotFoundException;
import com.onedayoffer.taskdistribution.services.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "employees")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> getEmployees(@RequestParam(required = false) String sort) throws EmployeeNotFoundException {
        return employeeService.getEmployees(sort);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO getOneEmployee(@PathVariable Integer id) throws EmployeeNotFoundException {
        return employeeService.getOneEmployee(id);
    }

    @GetMapping("{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasksByEmployeeId(@PathVariable Integer id) throws EmployeeNotFoundException {
        return employeeService.getTasksByEmployeeId(id);
    }

    @PatchMapping("{id}/tasks/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeTaskStatus(@PathVariable Integer id, TaskStatus status) throws TaskNotFoundException {
        employeeService.changeTaskStatus(id, status);
    }

    @PostMapping("/employees/{employeeId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void postNewTask(Integer employeeId, TaskDTO newTask) throws EmployeeNotFoundException {
        employeeService.postNewTask(employeeId, newTask);
    }
}