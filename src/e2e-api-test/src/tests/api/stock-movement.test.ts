import { api } from '../../utils/request';

describe('POST /api/stock/movement', () => {
    it('should create a stock movement', async () => {
        const payload = {
            itemId: 1,
            location: 'MainWarehouse',
            type: 1,
            quantityChange: 10.5,
            note: 'Restock'
        };

        const response = await api
            .post('/api/stock/movement')
            .send(payload)
            .expect(200);

        expect(response.body).toHaveProperty('success', true);
        expect(response.body).toHaveProperty('data');
        expect(response.body.data).toHaveProperty('success');
    });
});
