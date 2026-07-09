package lv.bootcamp.shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/animals")
@Tag(
        name = "Animals",
        description = "Operations for managing shelter animals"
)
public class AnimalApiController {

    private final AnimalService animalService;

    @Operation(
            summary = "Get all animals",
            description = "Returns every animal currently stored in the shelter."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Animals returned successfully"
    )
    @GetMapping
    public List<AnimalResponse> findAll() {

        return animalService.findAll();
    }

    @Operation(
            summary = "Get animal by ID",
            description = "Returns one animal using its ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Animal found"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Animal not found"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> findById(
            @PathVariable Long id) {

        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get adopted animals",
            description = "Returns only adopted animals."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Adopted animals returned"
    )
    @GetMapping("/adopted")
    public List<AnimalResponse> findAdopted() {

        return animalService.findAdopted();
    }

    @Operation(
            summary = "Create animal",
            description = "Creates a new animal in the shelter."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Animal created"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse create(
            @RequestBody @Valid AnimalCreateRequest request) {

        return animalService.create(request);
    }

    @Operation(
            summary = "Adopt animal",
            description = "Marks an animal as adopted."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Animal adopted"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Animal not found"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Animal already adopted"
    )
    @PostMapping("/{id}/adopt")
    public ResponseEntity<AnimalResponse> adopt(
            @PathVariable Long id,
            Authentication authentication) {

        return animalService.adopt(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleAlreadyAdopted(
            IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

}