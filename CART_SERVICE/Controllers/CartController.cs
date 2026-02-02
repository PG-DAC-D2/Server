using CartService.Application.DTOs;
using CartService.Application.Services;
using CartService.Domain.Entities;
using Microsoft.AspNetCore.Mvc;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;

namespace CartService.Controllers;
[ApiController]
[Route("cart")]
public class CartController : ControllerBase
{
    private readonly CartManager _cartManager;

    public CartController(CartManager cartManager)
    {
        _cartManager = cartManager;
    }

    private string? GetUserId()
    {
        try
        {
            // Prefer the authenticated principal if available
            var user = HttpContext?.User;
            if (user?.Identity != null && user.Identity.IsAuthenticated)
            {
                var userId = user.FindFirst("user_id")?.Value
                             ?? user.FindFirst("sub")?.Value
                             ?? user.FindFirst(ClaimTypes.NameIdentifier)?.Value
                             ?? user.FindFirst("nameid")?.Value;
                if (!string.IsNullOrEmpty(userId))
                    return userId;
            }

            // Fallback: parse Authorization header JWT token (without signature validation)
            if (Request.Headers.TryGetValue("Authorization", out var authHeader) &&
                !string.IsNullOrWhiteSpace(authHeader))
            {
                var token = authHeader.ToString().Replace("Bearer ", "", StringComparison.OrdinalIgnoreCase).Trim();
                if (!string.IsNullOrEmpty(token))
                {
                    var jwtHandler = new JwtSecurityTokenHandler();
                    if (jwtHandler.CanReadToken(token))
                    {
                        var jwtToken = jwtHandler.ReadJwtToken(token);
                        var userId = jwtToken.Claims.FirstOrDefault(claim =>
                                       claim.Type == "user_id" ||
                                       claim.Type == "sub" ||
                                       claim.Type == ClaimTypes.NameIdentifier ||
                                       claim.Type == "nameid")?.Value;
                        if (!string.IsNullOrEmpty(userId))
                            return userId;
                    }
                }
            }
        }
        catch
        {
            // Ignore and return null for unauthenticated
        }

        return null;
    }

    [HttpGet]
    public async Task<IActionResult> GetCart()
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        var cart = await _cartManager.GetOrCreateCartAsync(userId);
        return Ok(cart);
    }

    [HttpPost("items")]
    public async Task<IActionResult> AddItem([FromBody] AddCartItemDto dto)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        var item = new CartItem
        {
            ProductId = dto.ProductId,
            ProductName = dto.ProductName,
            UnitPrice = dto.UnitPrice,
            Quantity = dto.Quantity
        };

        await _cartManager.AddItemAsync(userId, item);
        return Ok();
    }

    [HttpPut("items/{cartItemId:guid}")]
    public async Task<IActionResult> UpdateQuantity(Guid cartItemId, [FromBody] UpdateQuantityDto dto)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.UpdateQuantityAsync(userId, cartItemId, dto.Quantity);
        return NoContent();
    }

    [HttpDelete("items/{cartItemId:guid}")]
    public async Task<IActionResult> RemoveItem(Guid cartItemId)
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.RemoveItemAsync(userId, cartItemId);
        return NoContent();
    }

    [HttpDelete("clear")]
    public async Task<IActionResult> ClearCart()
    {
        var userId = GetUserId();
        if (userId == null)
            return Unauthorized();

        await _cartManager.ClearCartAsync(userId);
        return NoContent();
    }
}
