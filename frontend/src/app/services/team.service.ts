import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Team {
  id: number;
  name: string;
  description: string;
  status: string;
  memberCount: number;
  resourceCount: number;
  createdAt: string;
}

export interface TeamMember {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface TeamResource {
  id: number;
  name: string;
  type: string;
  category: string;
  accessType: string;
  description: string;
  status: string;
  resourceUrl?: string;
  filePath?: string;
  fileSize?: number;
  mimeType?: string;
  fileExtension?: string;
  fileData?: string; // Base64 encoded file data (optional, for small files)
  createdBy?: string;
  uploadedBy?: string;
  createdAt?: string;
  allowedUserGroups?: string;
}

export interface AccessRequest {
  id: number;
  userId?: number;
  userName?: string;
  requestedBy: string;
  resourceId?: number;
  resourceName: string;
  status: string;
  requestedAt: string;
  justification?: string;
  requestedAccessLevel?: string;
  approverComments?: string;
  approvedBy?: string;
  approvedAt?: string;
}

export interface CreateResourceRequest {
  name: string;
  description: string;
  type: string;
  category?: string;
  accessType?: string;
  resourceUrl?: string;
  filePath?: string;
  fileSize?: number;
  mimeType?: string;
  fileExtension?: string;
  isGlobal: boolean;
  projectId: number;
  allowedUserGroups?: string;
}

export interface AccessRequestDTO {
  resourceId: number;
  requestedAccessLevel: string;
  justification: string;
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
  accessRequestId?: number;
}

export interface ResourceAccess {
  userId: number;
  username: string;
  email: string;
  accessLevel: string;
}

export interface ChatMessage {
  id: number;
  teamId: number;
  sender: string;
  message: string;
  timestamp: string;
  isCurrentUser?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getCurrentUserId(): number {
    const user = (this.authService as any).currentUserSubject?.value;
    // Map username to correct database user ID
    const userIdMap: { [key: string]: number } = {
      'rajesh.admin': 1,
      'priya.manager': 2, 
      'james.manager': 3,
      'anita.teamlead': 4,
      'carlos.teamlead': 5,
      'arjun.dev': 6,
      'emily.dev': 7,
      'vikram.dev': 8,
      'sophia.test': 9,
      'ravi.test': 10
    };
    return userIdMap[user?.username] || 1;
  }

  getCurrentUsername(): string {
    const user = (this.authService as any).currentUserSubject?.value;
    return user?.username || '';
  }

  getCurrentUserRole(): string {
    const user = (this.authService as any).currentUserSubject?.value;
    return user?.role || 'USER';
  }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/projects/my-projects`, { headers: this.getHeaders() });
  }

  getAllTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/projects`, { headers: this.getHeaders() });
  }

  getTeamById(id: number): Observable<Team> {
    return this.http.get<Team>(`${this.apiUrl}/projects/${id}`, { headers: this.getHeaders() });
  }

  getTeamMembers(teamId: number): Observable<TeamMember[]> {
    return this.http.get<TeamMember[]>(`${this.apiUrl}/projects/${teamId}/users`, { headers: this.getHeaders() });
  }

  getTeamResources(teamId: number): Observable<TeamResource[]> {
    return this.http.get<TeamResource[]>(`${this.apiUrl}/projects/${teamId}/resources`, { headers: this.getHeaders() });
  }

  getAccessRequests(teamId: number): Observable<AccessRequest[]> {
    return this.http.get<AccessRequest[]>(`${this.apiUrl}/access-requests/project/${teamId}`, { headers: this.getHeaders() });
  }

  createResource(resource: CreateResourceRequest): Observable<TeamResource> {
    return this.http.post<TeamResource>(`${this.apiUrl}/resources`, resource, { headers: this.getHeaders() });
  }

  uploadFile(file: File, projectId: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('projectId', projectId.toString());
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
      // Don't set Content-Type, let browser set it with boundary for multipart/form-data
    });
    
    return this.http.post(`${this.apiUrl}/resources/upload`, formData, { headers });
  }

  createResourceWithFile(file: File, resourceData: CreateResourceRequest): Observable<TeamResource> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', resourceData.name);
    formData.append('description', resourceData.description);
    formData.append('type', resourceData.type);
    formData.append('category', resourceData.category || 'OTHER');
    formData.append('accessType', resourceData.accessType || 'COMMON');

    formData.append('isGlobal', resourceData.isGlobal.toString());
    formData.append('projectId', resourceData.projectId.toString());
    
    if (resourceData.resourceUrl) {
      formData.append('resourceUrl', resourceData.resourceUrl);
    }
    
    if (resourceData.allowedUserGroups) {
      formData.append('allowedUserGroups', resourceData.allowedUserGroups);
    }
    
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
    
    return this.http.post<TeamResource>(`${this.apiUrl}/resources/upload`, formData, { headers });
  }

  getResourceAccess(resourceId: number): Observable<ResourceAccess[]> {
    return this.http.get<ResourceAccess[]>(`${this.apiUrl}/permissions/resource/${resourceId}`, { headers: this.getHeaders() });
  }

  updateTeamStatus(teamId: number, status: string): Observable<any> {
    const endpoint = status === 'ACTIVE' ? 'activate' : 
                    status === 'INACTIVE' ? 'deactivate' : 
                    status === 'COMPLETED' ? 'complete' : 
                    status === 'ARCHIVED' ? 'archive' : 'activate';
    return this.http.put(`${this.apiUrl}/projects/${teamId}/${endpoint}`, {}, { headers: this.getHeaders() });
  }

  getResourcesByCategory(projectId: number, category: string): Observable<TeamResource[]> {
    return this.http.get<TeamResource[]>(`${this.apiUrl}/resources/project/${projectId}/category/${category}`, { headers: this.getHeaders() });
  }

  getResourcesByAccessType(projectId: number, accessType: string): Observable<TeamResource[]> {
    return this.http.get<TeamResource[]>(`${this.apiUrl}/resources/project/${projectId}/access-type/${accessType}`, { headers: this.getHeaders() });
  }

  getCommonResources(projectId: number): Observable<TeamResource[]> {
    return this.http.get<TeamResource[]>(`${this.apiUrl}/resources/project/${projectId}/common`, { headers: this.getHeaders() });
  }

  getManagerControlledResources(projectId: number): Observable<TeamResource[]> {
    return this.http.get<TeamResource[]>(`${this.apiUrl}/resources/project/${projectId}/manager-controlled`, { headers: this.getHeaders() });
  }

  // Access Request methods
  createAccessRequest(username: string, request: AccessRequestDTO, projectId?: number): Observable<any> {
    const requestBody = {
      userId: this.getCurrentUserId(),
      resourceId: request.resourceId,
      requestedAccessLevel: request.requestedAccessLevel,
      justification: request.justification
    };
    return this.http.post(`${this.apiUrl}/access-requests/simple`, requestBody, { headers: this.getHeaders() });
  }

  getUserAccessRequests(userId: number): Observable<AccessRequest[]> {
    return this.http.get<AccessRequest[]>(`${this.apiUrl}/access-requests/user/${userId}`, { headers: this.getHeaders() });
  }

  getPendingRequestsForApprover(approverId: number): Observable<AccessRequest[]> {
    return this.http.get<AccessRequest[]>(`${this.apiUrl}/access-requests/pending`, { headers: this.getHeaders() });
  }

  approveAccessRequest(requestId: number, approverId: number, comments?: string): Observable<any> {
    const params = comments ? `?approverId=${approverId}&approverComments=${comments}` : `?approverId=${approverId}`;
    return this.http.put(`${this.apiUrl}/access-requests/${requestId}/approve${params}`, {}, { headers: this.getHeaders() });
  }

  rejectAccessRequest(requestId: number, approverId: number, comments?: string): Observable<any> {
    const params = comments ? `?approverId=${approverId}&approverComments=${comments}` : `?approverId=${approverId}`;
    return this.http.put(`${this.apiUrl}/access-requests/${requestId}/reject${params}`, {}, { headers: this.getHeaders() });
  }

  // Notification methods
  getUserNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications/user/${userId}`, { headers: this.getHeaders() });
  }

  getUnreadNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications/user/${userId}/unread`, { headers: this.getHeaders() });
  }

  getUnreadNotificationCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/notifications/user/${userId}/count`, { headers: this.getHeaders() });
  }

  markNotificationAsRead(notificationId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/notifications/${notificationId}/read`, {}, { headers: this.getHeaders() });
  }

  markAllNotificationsAsRead(userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/notifications/user/${userId}/read-all`, {}, { headers: this.getHeaders() });
  }

  // File download/viewing methods
  getFileDownloadUrl(resourceId: number): string {
    return `${this.apiUrl}/resources/${resourceId}/download`;
  }

  downloadFile(resourceId: number): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
    return this.http.get(`${this.apiUrl}/resources/${resourceId}/download`, { 
      headers, 
      responseType: 'blob' 
    });
  }

  updateExistingResourcesWithUserGroups(): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/resources/update-user-groups`, {}, { headers: this.getHeaders() });
  }
  
  updateResourceAccessSettings(resourceId: number, accessSettings: { accessType?: string, allowedUserGroups?: string }): Observable<TeamResource> {
    return this.http.put<TeamResource>(`${this.apiUrl}/resources/${resourceId}/access-settings`, accessSettings, { headers: this.getHeaders() });
  }
  
  revokeUserAccess(userId: number, resourceId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/permissions/user/${userId}/resource/${resourceId}`, { headers: this.getHeaders() });
  }
  
  // Team Chat methods
  getTeamChatMessages(teamId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/teams/${teamId}/chat/messages`, { headers: this.getHeaders() });
  }
  
  sendChatMessage(teamId: number, message: string): Observable<ChatMessage> {
    const messageData = {
      teamId: teamId,
      sender: this.getCurrentUsername(),
      message: message,
      timestamp: new Date().toISOString()
    };
    return this.http.post<ChatMessage>(`${this.apiUrl}/teams/${teamId}/chat/messages`, messageData, { headers: this.getHeaders() });
  }
  
  logout(): void {
    this.authService.logout();
  }
}