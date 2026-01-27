using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace OrderService.Domain.Entities;

public class OrderItem
{
    [Key]
    public Guid OrderItemId { get; set; }

    public Guid OrderId { get; set; }

    [JsonIgnore]
    public Order? Order { get; set; }

    public string ProductId { get; set; } = default!;
    public string ProductName { get; set; } = default!;
    public decimal UnitPrice { get; set; }
    public int Quantity { get; set; }
}
