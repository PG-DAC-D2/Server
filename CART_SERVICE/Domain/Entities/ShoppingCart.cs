using System.ComponentModel.DataAnnotations;

namespace CartService.Domain.Entities;

public class ShoppingCart
{
    [Key]
    public Guid CartId { get; set; }

    [Required]
    public string UserId { get; set; } = default!;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public List<CartItem> Items { get; set; } = new();
}
