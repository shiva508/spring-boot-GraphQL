package com.ghraphql.controller;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ghraphql.model.Student;
import com.ghraphql.repository.StudentRepository;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
public class StudentController {
	@Autowired
	private StudentRepository repository;
	@Value("classpath:student.graphqls")
	private Resource schemaResource;

	
	private GraphQL  graphQL;
	@PostConstruct
	public void loadSchema() throws IOException {
		File schemaFile=schemaResource.getFile();
		TypeDefinitionRegistry registry=new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring=buildWiring();
		GraphQLSchema schema=new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphQL=GraphQL.newGraphQL(schema).build();
	}
	
	
	
	
	public RuntimeWiring buildWiring() {
		
		DataFetcher<List<Student>> fetcher1=data->{
			return repository.findAll();
		};
		DataFetcher<Student> fetcher2=data->{
			return repository.findByAddress(data.getArgument("address"));
		};
		return RuntimeWiring.newRuntimeWiring().type("Query",
				typeWriting -> typeWriting.dataFetcher("getAllStudents", fetcher1).dataFetcher("findStudent", fetcher2))
				.build();
	}


	@PostMapping("/we")
	public ResponseEntity<Object> getAllQL(@RequestBody String Query){
		ExecutionResult result=graphQL.execute(Query);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}

	@PostMapping("/i")
	public ResponseEntity<Object> getSingle(@RequestBody String Query){
		ExecutionResult result=graphQL.execute(Query);
		return new ResponseEntity<>(result,HttpStatus.OK);
	}
	@GetMapping("/save")
	public String saveData() {
		Student student = new Student();
		student.setAddress("tmp");
		student.setName("shiva");
		student.setPassportNumber("00000");

		Student student1 = new Student();
		student1.setAddress("tm");
		student1.setName("sh");
		student1.setPassportNumber("00000");
		repository.saveAll(Arrays.asList(student, student1));
		return "records saved";
	}

	@GetMapping("/all")
	public List<Student> getAll() {
		return repository.findAll();
	}
}
