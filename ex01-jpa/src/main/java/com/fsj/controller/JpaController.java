package com.fsj.controller;

import com.fsj.dao.PersonRepository;
import com.fsj.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jpa")
public class JpaController {
	//1 Spring Data JPA已自动为你注册bean，所以可自动注入
	@Autowired
	PersonRepository personRepository;

	@RequestMapping("/save")
	public Person save(String name,String address,Integer age){
		Person p = (Person) personRepository.save(new Person(null, name, age, address));
		return p; //Spring会将实体类对象自动输出为REST资源
	}

	/**
	 * 测试findByAddress
	 */
	@RequestMapping("/q1")
	public List<Person> q1(String address){
		List<Person> people = personRepository.findByAddress(address);
		return people;
	}
	
	/**
	 * 测试findByNameAndAddress
	 */
	@RequestMapping("/q2")
	public Person q2(String name,String address){
		Person people = personRepository.findByNameAndAddress(name, address);
		return people;
	}
	
	/**
	 * 测试withNameAndAddressQuery
	 */
	@RequestMapping("/q3")
	public Person q3(String name,String address){
		Person p = personRepository.withNameAndAddressQuery(name, address);
		return p;
	}
	
	/**
	 * 测试withNameAndAddressNamedQuery
	 */
	@RequestMapping("/q4")
	public Person q4(String name,String address){
		Person p = personRepository.withNameAndAddressNamedQuery(name, address);
		return p;
	}

	/**
	 * 测试排序，按照年龄升序
	 */
	@RequestMapping("/sort")
	public List<Person> sort(){
		List<Person> people = personRepository.findAll(new Sort(Direction.ASC,"age"));
		return people;
	}
	
	/**
	 * 测试分页
	 */
	@RequestMapping("/page")
	public Page<Person> page(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
							 @RequestParam(value = "pageSize", defaultValue = "2") Integer pageSize){
		Page<Person> pagePeople = personRepository.findAll(new PageRequest(pageNum, pageSize));
		return pagePeople;
	}
}
/* page output

{
  "content" : [ {
    "id" : 3,
    "name" : "yy",
    "age" : 30,
    "address" : "上海"
  }, {
    "id" : 4,
    "name" : "zz",
    "age" : 29,
    "address" : "南京"
  } ],
  "last" : false,
  "totalElements" : 6,
  "totalPages" : 3,
  "size" : 2,
  "number" : 1,
  "sort" : null,
  "first" : false,
  "numberOfElements" : 2
}
 */