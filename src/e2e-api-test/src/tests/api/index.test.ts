import request from 'supertest';

describe('GET /api/index', () => {
    it('should return 200 with expected structure', async () => {
        const res = await request('http://localhost:30308').get('/api/index');
        expect(res.status).toBe(200);
        expect(res.body).toHaveProperty('success');
        expect(res.body).toHaveProperty('data');
    });
});
