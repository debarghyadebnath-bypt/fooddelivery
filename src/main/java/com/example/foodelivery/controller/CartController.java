        package com.example.foodelivery.controller;

        import com.example.foodelivery.dto.CartDto;
        import com.example.foodelivery.dto.CartItemRequestDto;
        import com.example.foodelivery.entity.User;
        import com.example.foodelivery.service.CartService;
        import jakarta.validation.Valid;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.annotation.AuthenticationPrincipal;
        import org.springframework.web.bind.annotation.*;

        @RestController
        @RequestMapping("/cart")
        public class CartController {
            private final CartService cartService;

            public CartController(CartService cartService) {
                this.cartService = cartService;
            }

            @PostMapping("/add-item")
            public ResponseEntity<CartDto> addItem(@AuthenticationPrincipal User authenticatedUser,
                                                   @Valid @RequestBody CartItemRequestDto cartItemRequestDto) {

                CartDto cartDto = cartService.addItem( authenticatedUser.getId(),cartItemRequestDto);
                return new ResponseEntity<>(cartDto, HttpStatus.OK);
            }

            @PutMapping("/update-item")
            public ResponseEntity<CartDto> updateItem(@AuthenticationPrincipal User authenticatedUser , @Valid @RequestBody CartItemRequestDto cartItemRequestDto) {

                CartDto cartDto = cartService.updateItem(authenticatedUser.getId(), cartItemRequestDto);
                return new ResponseEntity<>(cartDto, HttpStatus.OK);
            }

            @DeleteMapping("/remove-item/{menuItemId}")
            public ResponseEntity<CartDto> removeItem(@AuthenticationPrincipal User authenticatedUser,  @PathVariable
            Long menuItemId) {

                CartDto cartDto = cartService.removeItem(authenticatedUser.getId(), menuItemId);
                return new ResponseEntity<>(cartDto, HttpStatus.OK);
            }

            @GetMapping
            public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal User authenticatedUser) {
                if (authenticatedUser == null) {
                    throw new IllegalStateException("Authenticated user is required to access cart");
                }
                CartDto cartDto = cartService.getCart(authenticatedUser.getId());
                return new ResponseEntity<>(cartDto, HttpStatus.OK);
            }
        }