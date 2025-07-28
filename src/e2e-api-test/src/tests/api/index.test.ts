import request from 'supertest';
import {api} from "../../utils/request";

describe('GET /api/index', () => {
    it('should return 200 with expected structure', async () => {
        const res = await api.get('/api/index');
        expect(res.status).toBe(200);
        expect(res.body).toHaveProperty('success');
        expect(res.body).toHaveProperty('data');
    });
});
