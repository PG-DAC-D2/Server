using CartService.Domain.Entities;
using System.Text.Json.Serialization;

public class CartItem
{
    public Guid CartItemId { get; set; }
    public Guid CartId { get; set; }

    [JsonIgnore]   // Prevent JSON serialization cycle for Cart reference
    public ShoppingCart? Cart { get; set; }

    public string ProductId { get; set; } = default!;
    public string ProductName { get; set; } = default!;
    public decimal UnitPrice { get; set; }
    public int Quantity { get; set; }
}
