package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.controllers.exceptions.EmployeeNotFoundException;
import com.onedayoffer.taskdistribution.controllers.exceptions.TaskNotFoundException;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) throws EmployeeNotFoundException {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && !sortDirection.isEmpty()) {
            if (sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }

        List<Employee> employees;
        employees = employeeRepository.findAll(Sort.by(direction, "fio"));

        if (employees.isEmpty()){
            log.info("Employees not found");
            throw new EmployeeNotFoundException();
        }
        Type listType = new TypeToken<List<EmployeeDTO>>() {
        }.getType();
        return modelMapper.map(employees, listType);
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) throws EmployeeNotFoundException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            log.error("Employee not found");
            throw new EmployeeNotFoundException();
        }
        Type type = new TypeToken<EmployeeDTO>() {
        }.getType();
        return modelMapper.map(employee.get(), type);
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) throws EmployeeNotFoundException {
        List<Task> taskList = taskRepository.findByEmployeeId(id);
        if (taskList.isEmpty()){
            log.info("Cannot find this id of employee");
            throw new EmployeeNotFoundException();
        }
        Type listType = new TypeToken<List<TaskDTO>>() {
        }.getType();
        return modelMapper.map(taskList, listType);
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) throws TaskNotFoundException {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(status);
            taskRepository.save(task);
        } else {
            log.error("Task not found");
            throw new TaskNotFoundException();
        }
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) throws EmployeeNotFoundException {
        if (newTask == null) {
            log.info("New task is null");
            throw new java.lang.UnsupportedOperationException("New task is empty");
        }
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            log.info("Employee not found");
            throw new EmployeeNotFoundException();
        }

        Task task = modelMapper.map(newTask, Task.class);
        task.setEmployee(employee.get());
        taskRepository.save(task);
    }
}
