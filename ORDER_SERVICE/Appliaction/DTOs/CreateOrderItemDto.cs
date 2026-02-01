namespace OrderService.Application.DTOs;

public class CreateOrderItemDto
{
    public string ProductId { get; set; } = default!;
    public string ProductName { get; set; } = default!;
    public decimal UnitPrice { get; set; }
    public int Quantity { get; set; }
}
